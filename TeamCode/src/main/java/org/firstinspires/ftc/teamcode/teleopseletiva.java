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
 *   Gamepad 1 (Piloto):
 *   - Analógico esquerdo ........... mover robô (frente/trás e lateral)
 *   - Analógico direito (X) ........ girar robô
 *
 *   Gamepad 2 (Operador) / Gamepad 1:
 *   - Left Bumper (L1) / Botão A ... Ligar Shooter PÓLEN (2650 RPM) + abre Trava 2 ao atingir velocidade
 *   - Right Bumper (R1) / Botão B .. Ligar Shooter NÉCTAR (2800 RPM) + abre Trava 1 ao atingir velocidade
 *   - Analógico esquerdo (Y) ....... controle proporcional do intake (frente = coleta, trás = expele)
 *   - Left Trigger (LT / L2) ....... coleta com o intake (100%)
 *   - Right Trigger (RT / R2) ...... expele com o intake (100%)
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

    /** true quando o shooter está sendo acionado (L1 ou R1 pressionado). */
    private boolean shooterLigado = false;

    /** Qual tiro foi acionado por último: pólen (LB/A) ou néctar (RB/B). */
    private enum ModoTiro { POLEN, NECTAR }

    private ModoTiro modoTiro = ModoTiro.POLEN;

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
            controlarShooterETravas();
            controlarIntake();
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

    /** Analógico esquerdo move o robô (relativo à quadra) e o analógico direito gira (Gamepad 1). */
    private void controlarChassi() {
        if (gamepad1.options || gamepad1.back) {
            chassi.zerarAngulo();
        }

        chassi.moverRelativoAQuadra(
                -gamepad1.left_stick_y,   // frente/trás
                gamepad1.left_stick_x,   // lateral
                -gamepad1.right_stick_x); // rotação
    }

    /**
     * Controla o shooter e as travas de disparo (Pólen e Néctar).
     *
     * CONTROLES:
     * - Left Bumper (L1) ou A: Liga o shooter na velocidade do PÓLEN (Shooter.VELOCIDADE_POLEN_RPM)
     *   e abre a Trava 2 (pólen) assim que a velocidade ideal é atingida.
     * - Right Bumper (R1) ou B: Liga o shooter na velocidade do NÉCTAR (Shooter.VELOCIDADE_NECTAR_RPM)
     *   e abre a Trava 1 (néctar) assim que a velocidade ideal é atingida.
     */
    private void controlarShooterETravas() {
        boolean acionarPolen = gamepad2.left_bumper || gamepad1.left_bumper || gamepad2.a || gamepad1.a;
        boolean acionarNectar = gamepad2.right_bumper || gamepad1.right_bumper || gamepad2.b || gamepad1.b;

        if (acionarPolen) {
            modoTiro = ModoTiro.POLEN;
            shooterLigado = true;
            shooter.ligarPolen();

            if (shooter.estaProntoParaAtirarPolen()) {
                liberador.liberarTrava2();
                } else {
                liberador.segurarTrava2();
                }
            

        } else if (acionarNectar) {
            modoTiro = ModoTiro.NECTAR;
            shooterLigado = true;
            shooter.ligarNectar();

            if (shooter.estaProntoParaAtirarNectar()) {
                liberador.liberarTrava1();
                } else {
                    liberador.segurarTrava1();
                }

        } else {
            shooterLigado = false;
            shooter.parar();
            liberador.segurarTrava1();
            liberador.segurarTrava2();
        }
    }

    /** Controla o intake (via analógico Y ou triggers). */
    private void controlarIntake() {
        float leftTrigger = Math.max(gamepad2.left_trigger, gamepad1.left_trigger);
        float rightTrigger = Math.max(gamepad2.right_trigger, gamepad1.right_trigger);
        double analogicoIntake = gamepad2.left_stick_y;

        if (leftTrigger > 0.1f) {
            intake.coletar();
        } else if (rightTrigger > 0.1f) {
            intake.expelir();
        } else if (Math.abs(analogicoIntake) > 0.1) {
            intake.controlarPorAnalogico(analogicoIntake);
        } else {
            intake.parar();
        }
    }

    /** Mostra no Driver Station o estado de todos os subsistemas. */
    private void atualizarTelemetria() {
        telemetry.addData("Tempo de partida", tempoDePartida.toString());
        telemetry.addData("Shooter", shooterLigado ? "LIGADO (" + modoTiro + ")" : "DESLIGADO");
        telemetry.addData("Shooter pronto (Pólen)", shooter.estaProntoParaAtirarPolen() ? "SIM" : "NÃO");
        telemetry.addData("Shooter pronto (Néctar)", shooter.estaProntoParaAtirarNectar() ? "SIM" : "NÃO");
        telemetry.addData("Shooter alvo (RPM)", shooter.getVelocidadeAlvoRPM());
        telemetry.addData("Shooter média (RPM)", shooter.getVelocidadeMediaRPM());
        telemetry.addData("Shooter 1 (RPM)", shooter.getVelocidadeShooter1RPM());
        telemetry.addData("Shooter 2 (RPM)", shooter.getVelocidadeShooter2RPM());
        telemetry.addData("Shooter 3 (RPM)", shooter.getVelocidadeShooter3RPM());
        telemetry.addData("Shooter 1 (ticks/s)", shooter.getVelocidadeShooter1TicksPorSegundo());
        telemetry.addData("Shooter 2 (ticks/s)", shooter.getVelocidadeShooter2TicksPorSegundo());
        telemetry.addData("Shooter 3 (ticks/s)", shooter.getVelocidadeShooter3TicksPorSegundo());
        telemetry.addData("Tipo de motor na configuração", shooter.getTipoDeMotorConfigurado());
        telemetry.addData("Indexers", indexer.estaGirando() ? "GIRANDO" : "PARADOS");
        telemetry.addData("Trava 1 (néctar)", liberador.getEstadoTrava1());
        telemetry.addData("Trava 2 (pólen)", liberador.getEstadoTrava2());
        telemetry.addData("Intake", intake.getEstadoAtual());
        telemetry.update();
    }
}
