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
 * CONTROLES (somente o gamepad 1 é usado)
 *   - Analógico esquerdo ... mover (frente/trás e para os lados), relativo ao campo
 *   - Analógico direito .... girar
 *   - L1 ................... liga/desliga o shooter (sobe até VELOCIDADE_ALVO_RPM)
 *   - A .................... ativa o MODO TIRO:
 *                              * os indexers giram levando o pólen até o liberador;
 *                              * o liberador segura o pólen e só o solta para o
 *                                shooter quando ele está acima de
 *                                VELOCIDADE_MINIMA_PARA_ATIRAR_RPM.
 *                            O modo tiro continua ativo até o R1 ser apertado.
 *   - R1 (segurar) ......... coleta com o intake e cancela o modo tiro
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

    /** true depois que o A é apertado; só volta a false quando o R1 é apertado. */
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

    /** Analógico esquerdo move o robô (relativo ao campo) e o analógico direito gira. */
    private void controlarChassi() {
        chassi.moverRelativoAoCampo(
                gamepad1.left_stick_y,   // frente/trás
                gamepad1.left_stick_x,   // lateral
                gamepad1.right_stick_x); // rotação
    }

    /** Cada aperto do L1 liga ou desliga o shooter. */
    private void controlarShooter() {
        boolean botaoL1Atual = gamepad1.left_bumper;
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
     * Controla o intake (R1) e o modo tiro (A).
     *
     * - Apertar A ativa o modo tiro.
     * - Segurar R1 coleta com o intake e cancela o modo tiro.
     * - No modo tiro, os indexers giram o tempo todo e o liberador só solta o pólen
     *   quando o shooter está rápido o suficiente; se a velocidade cair
     *   (por exemplo, logo após um disparo), o liberador volta a segurar e espera.
     * - Fora do modo tiro, o liberador fica sempre segurando.
     */
    private void controlarIntakeEModoTiro() {
        // Botão A: ativa o modo tiro (no momento do aperto)
        boolean botaoAAtual = gamepad1.a;
        if (botaoAAtual && !botaoAAnterior) {
            modoTiroAtivo = true;
        }
        botaoAAnterior = botaoAAtual;

        // Botão R1: coleta e cancela o modo tiro
        if (gamepad1.right_bumper) {
            modoTiroAtivo = false;
            intake.coletar();
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
        telemetry.addData("Ângulo do robô (graus)", chassi.getAnguloEmGraus());
        telemetry.addData("Shooter", shooterLigado ? "LIGADO" : "DESLIGADO");
        telemetry.addData("Shooter pronto para atirar", shooter.estaProntoParaAtirar() ? "SIM" : "NÃO");
        telemetry.addData("Shooter alvo (RPM)", shooter.getVelocidadeAlvoRPM());
        telemetry.addData("Shooter média (RPM)", shooter.getVelocidadeMediaRPM());
        telemetry.addData("Shooter 1 (RPM)", shooter.getVelocidadeShooter1RPM());
        telemetry.addData("Shooter 2 (RPM)", shooter.getVelocidadeShooter2RPM());
        // Valores crus do encoder, para conferir a conversão para RPM
        telemetry.addData("Shooter 1 (ticks/s)", shooter.getVelocidadeShooter1TicksPorSegundo());
        telemetry.addData("Shooter 2 (ticks/s)", shooter.getVelocidadeShooter2TicksPorSegundo());
        telemetry.addData("Tipo de motor na configuração", shooter.getTipoDeMotorConfigurado());
        telemetry.addData("Modo tiro", modoTiroAtivo ? "ATIVO" : "INATIVO");
        telemetry.addData("Indexers", indexer.estaGirando() ? "GIRANDO" : "PARADOS");
        telemetry.addData("Liberador", liberador.getEstadoAtual());
        telemetry.addData("Intake", intake.getEstadoAtual());
        telemetry.update();
    }
}
