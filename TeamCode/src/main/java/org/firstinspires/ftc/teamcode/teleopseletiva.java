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
 *   - R1 (Right Bumper) ............ coleta rápida com o intake (100%) e cancela o modo tiro
 *   - B ............................ expele peças com o intake (100%)
 *   - L1 ........................... liga/desliga o shooter
 *   - A ............................ ativa o MODO TIRO
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

    /** true depois que o A é apertado; só volta a false quando o R1 ou o intake for acionado. */
    private boolean modoTiroAtivo = false;

    // Guardam se o botão estava apertado no ciclo anterior. Assim a ação
    // só acontece no momento em que o botão é apertado, e não enquanto ele é segurado.
    private boolean botaoL1Anterior = false;
    private boolean botaoAAnterior = false;

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
        if (!liberador.isConectado()) {
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
     * Controla o intake (via analógico, R1 / Right Bumper ou B) e o modo tiro (A).
     *
     * - Segurar R1 (Right Bumper): coleta com o intake (potência fixa/máxima) e cancela o modo tiro.
     * - Segurar B: expele peças com o intake (potência fixa/máxima).
     * - Analógico esquerdo Y (gamepad2): controle proporcional do intake
     *     * Empurrar para FRENTE (Y negativo) -> COLETA
     *     * Puxar para TRÁS (Y positivo) -> EXPELI
     * - Apertar A: ativa o MODO TIRO.
     */
    private void controlarIntakeEModoTiro() {
        // Botão A: ativa o modo tiro (no momento do aperto)
        boolean botaoAAtual = gamepad2.a || gamepad1.a;
        if (botaoAAtual && !botaoAAnterior) {
            modoTiroAtivo = true;
        }
        botaoAAnterior = botaoAAtual;

        // Leitura dos botões e do analógico esquerdo Y do gamepad2
        boolean rightBumper = gamepad2.right_bumper || gamepad1.right_bumper;
        boolean botaoB = gamepad2.b || gamepad1.b;
        double analogicoIntake = gamepad2.left_stick_y;

        // Prioridade de acionamento do intake: R1 > B > Analógico
        if (rightBumper) {
            modoTiroAtivo = false;
            intake.coletar();
        } else if (botaoB) {
            intake.expelir();
        } else if (Math.abs(analogicoIntake) > 0.1) {
            modoTiroAtivo = false;
            intake.controlarPorAnalogico(analogicoIntake);
        } else {
            intake.parar();
        }

        // Executa o modo tiro
        if (modoTiroAtivo) {
            indexer.alimentar();
            if (shooter.estaProntoParaAtirar()) {
                liberador.liberar();
            } else {
                liberador.segurar();
            }
        } else {
            indexer.parar();
            liberador.segurar();
        }
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
        telemetry.addData("Modo tiro", modoTiroAtivo ? "ATIVO" : "INATIVO");
        telemetry.addData("Indexers", indexer.estaGirando() ? "GIRANDO" : "PARADOS");
        telemetry.addData("Liberador", liberador.getEstadoAtual());
        telemetry.addData("Intake", intake.getEstadoAtual());
        telemetry.update();
    }
}
