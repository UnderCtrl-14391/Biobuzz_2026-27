package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Chassi;
import org.firstinspires.ftc.teamcode.subsystems.Indexer;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Liberador;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

/**
 * TeleOp principal ("seletiva").
 *
 * Este arquivo NÃO controla o hardware diretamente: ele só lê o gamepad
 * e diz a cada subsistema o que fazer. A lógica de cada mecanismo fica
 * na pasta "subsystems".
 *
 * CONTROLES:
 *   Gamepad 1:
 *   - Analógico esquerdo ........... mover robô (frente/trás e lateral)
 *   - Analógico direito (X) ........ girar robô
 *
 *   Gamepad 2:
 *   - Analógico esquerdo (Y) ....... controle proporcional do intake (frente = coleta, trás = expeli)
 *   - R1 (Right Bumper) ............ coleta rápida com o intake (100%)
 *   - L1 ........................... liga/desliga o shooter (serve para os dois tiros)
 *   - A ............................ atira 1 PÓLEN: abre a trava 2 por 0,15 s e fecha
 *   - B ............................ atira 1 NÉCTAR: abre a trava 1 por 0,15 s, fecha e o
 *                                    indexer enfileira o próximo; enquanto segurado,
 *                                    também expele com o intake (100%)
 */
@TeleOp(name = "seletiva")
public class teleopseletiva extends LinearOpMode {

    // ===================== SUBSISTEMAS =====================

    private Chassi chassi;
    private Intake intake;
    private Shooter shooter;
    private Indexer indexer;
    private Liberador liberador;

    /** Cronômetro do tempo de partida (mostrado na telemetria). */
    private final ElapsedTime tempoDePartida = new ElapsedTime();

    // ===================== ESTADO DO TELEOP =====================

    /** true quando o shooter foi ligado pelo L1. */
    private boolean shooterLigado = false;

    /** Qual tiro foi pedido por último: pólen (A) ou néctar (B). */
    private enum ModoTiro { POLEN, NECTAR }

    private ModoTiro modoTiro = ModoTiro.POLEN;

    /** Etapas de um disparo: fechado -> abre (0,15 s) -> enfileira (só néctar) -> fechado. */
    private enum EtapaTiro { FECHADO, ABERTO, ENFILEIRANDO }

    private EtapaTiro etapaTiro = EtapaTiro.FECHADO;

    /** Conta quanto tempo o ciclo de tiro está na etapa atual. */
    private final ElapsedTime tempoNaEtapa = new ElapsedTime();

    // Guardam se o botão estava apertado no ciclo anterior. Assim a ação
    // só acontece no momento em que o botão é apertado, e não enquanto ele é segurado.
    private boolean botaoL1Anterior = false;
    private boolean botaoAAnterior = false;
    private boolean botaoBAnterior = false;

    /**
     * Método principal do OpMode: inicializa os subsistemas, espera o START
     * e depois repete o ciclo de controle até a partida acabar.
     */
    @Override
    public void runOpMode() {
        inicializarSubsistemas();

        waitForStart();
        tempoDePartida.reset();

        while (opModeIsActive()) {
            controlarChassi();
            controlarShooter();
            controlarIntakeEModoTiro();
            atualizarTelemetria();
        }
    }

    /** Cria todos os subsistemas e avisa na telemetria se algum servo não foi encontrado. */
    private void inicializarSubsistemas() {
        chassi = new Chassi(hardwareMap);
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        indexer = new Indexer(hardwareMap);
        liberador = new Liberador(hardwareMap);

        if (!indexer.isConectado()) {
            telemetry.addData("Aviso", "indexers (flower / flower2) não encontrados como CRServo");
        }
        if (!liberador.isTrava1Conectada()) {
            telemetry.addData("Aviso", "liberador (trava1) não encontrado na configuração");
        }
        if (!liberador.isTrava2Conectada()) {
            telemetry.addData("Aviso", "liberador (trava2) não encontrado na configuração");
        }

        telemetry.addData("Status", "Inicializado");
        telemetry.update();
    }

    /** Analógico esquerdo move o robô (relativo ao robô) e o analógico direito gira. */
    private void controlarChassi() {
        chassi.moverRelativoAoRobo(
                gamepad1.left_stick_y,   // frente/trás
                gamepad1.left_stick_x,   // lateral
                gamepad1.right_stick_x); // rotação
    }

    /** Cada aperto do L1 liga ou desliga o shooter. */
    private void controlarShooter() {
        boolean botaoL1Atual = gamepad2.left_bumper;
        if (botaoL1Atual && !botaoL1Anterior) {
            shooterLigado = !shooterLigado;
        }
        botaoL1Anterior = botaoL1Atual;

        if (shooterLigado) {
            shooter.ligar();
        } else {
            shooter.parar();
        }
    }

    /**
     * Controla o intake (via analógico, R1 / Right Bumper ou B) e os disparos (A e B).
     *
     * - Apertar A: atira 1 pólen (ver executarDisparo).
     * - Apertar B: atira 1 néctar (e, enquanto segurado, expele com o intake).
     * - Segurar R1 (Right Bumper): coleta com o intake (potência fixa/máxima).
     * - Analógico esquerdo Y (gamepad2): controle proporcional do intake
     *     * Empurrar para FRENTE (Y negativo) -> COLETA
     *     * Puxar para TRÁS (Y positivo) -> EXPELI
     */
    private void controlarIntakeEModoTiro() {
        // A e B: cada aperto faz um disparo
        boolean botaoAAtual = gamepad2.a || gamepad1.a;
        boolean botaoBAtual = gamepad2.b;
        if (botaoAAtual && !botaoAAnterior) iniciarDisparo(ModoTiro.POLEN);
        if (botaoBAtual && !botaoBAnterior) iniciarDisparo(ModoTiro.NECTAR);
        botaoAAnterior = botaoAAtual;
        botaoBAnterior = botaoBAtual;

        // Leitura dos botões e do analógico esquerdo Y do gamepad2
        boolean rightBumper = gamepad2.right_bumper;
        double analogicoIntake = gamepad2.left_stick_y;

        // Prioridade de acionamento do intake: R1 > B > Analógico
        if (rightBumper) {
            intake.coletar();
        } else if (botaoBAtual) {
            intake.expelir();
        } else if (Math.abs(analogicoIntake) > 0.1) {
            intake.controlarPorAnalogico(analogicoIntake);
        } else {
            intake.parar();
        }

        executarDisparo();
    }

    /** Começa um disparo: abre a trava do tiro escolhido (reinicia se já estiver no meio de um). */
    private void iniciarDisparo(ModoTiro novoModo) {
        liberador.segurar();
        modoTiro = novoModo;
        mudarEtapaTiro(EtapaTiro.ABERTO);
    }

    /**
     * Um disparo por aperto de botão:
     *   1. ABERTO: abre a trava por Liberador.TEMPO_ABERTO_S (0,15 s).
     *        - pólen (A): trava 2;  néctar (B): trava 1.
     *   2. ENFILEIRANDO (só néctar): fecha a trava e o indexer gira devagar por
     *      Indexer.TEMPO_ENFILEIRAR_S para enfileirar o próximo néctar.
     *   3. FECHADO: travas fechadas e indexer parado até o próximo aperto.
     */
    private void executarDisparo() {
        switch (etapaTiro) {
            case ABERTO:
                if (modoTiro == ModoTiro.NECTAR) liberador.liberarTrava1(); else liberador.liberarTrava2();
                indexer.parar();
                if (tempoNaEtapa.seconds() >= Liberador.TEMPO_ABERTO_S) {
                    mudarEtapaTiro(modoTiro == ModoTiro.NECTAR ? EtapaTiro.ENFILEIRANDO : EtapaTiro.FECHADO);
                }
                break;

            case ENFILEIRANDO:
                liberador.segurar();
                indexer.enfileirar();
                if (tempoNaEtapa.seconds() >= Indexer.TEMPO_ENFILEIRAR_S) {
                    mudarEtapaTiro(EtapaTiro.FECHADO);
                }
                break;

            case FECHADO:
                liberador.segurar();
                indexer.parar();
                break;
        }
    }

    /** Troca a etapa do ciclo de tiro e zera o cronômetro da etapa. */
    private void mudarEtapaTiro(EtapaTiro novaEtapa) {
        etapaTiro = novaEtapa;
        tempoNaEtapa.reset();
    }

    /** Mostra no Driver Station o estado de todos os subsistemas. */
    private void atualizarTelemetria() {
        telemetry.addData("Tempo de partida", tempoDePartida.toString());
        telemetry.addData("Shooter", shooterLigado ? "LIGADO" : "DESLIGADO");
        telemetry.addData("Shooter pronto para atirar", shooter.estaProntoParaAtirar() ? "SIM" : "NÃO");
        telemetry.addData("Shooter alvo (RPM)", shooter.getVelocidadeAlvoRPM());
        telemetry.addData("Shooter média (RPM)", shooter.getVelocidadeMediaRPM());
        telemetry.addData("Shooter 1 (RPM)", shooter.getVelocidadeShooter1RPM());
        telemetry.addData("Shooter 2 (RPM)", shooter.getVelocidadeShooter2RPM());
        telemetry.addData("Shooter 3 (RPM)", shooter.getVelocidadeShooter3RPM());
        // Valores crus do encoder, para conferir a conversão para RPM
        telemetry.addData("Shooter 1 (ticks/s)", shooter.getVelocidadeShooter1TicksPorSegundo());
        telemetry.addData("Shooter 2 (ticks/s)", shooter.getVelocidadeShooter2TicksPorSegundo());
        telemetry.addData("Shooter 3 (ticks/s)", shooter.getVelocidadeShooter3TicksPorSegundo());
        telemetry.addData("Tipo de motor na configuração", shooter.getTipoDeMotorConfigurado());
        telemetry.addData("Último tiro", modoTiro);
        telemetry.addData("Indexers", indexer.estaGirando() ? "GIRANDO" : "PARADOS");
        telemetry.addData("Etapa do tiro", etapaTiro);
        telemetry.addData("Trava 1 (néctar)", liberador.getEstadoTrava1());
        telemetry.addData("Trava 2 (pólen)", liberador.getEstadoTrava2());
        telemetry.addData("Intake", intake.getEstadoAtual());
        telemetry.update();
    }
}
