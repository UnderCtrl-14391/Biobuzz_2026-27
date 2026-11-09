package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Chassi;
import org.firstinspires.ftc.teamcode.subsystems.Indexer;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Liberador;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

/**
 * TeleOp principal ("seletiva").
 *
 * CONTROLES:
 *   Gamepad 1 (Piloto - Tração apenas):
 *   - Analógico esquerdo ........... mover robô (frente/trás e lateral, relativo à quadra)
 *   - Analógico direito (X) ........ girar robô
 *   - Botão Y / Options / Back ..... zera ângulo da IMU (frente da quadra)
 *
 *   Gamepad 2 (Operador - Mecanismos apenas):
 *   - Left Bumper (L1) / Botão A ... Ligar Shooter PÓLEN (2650 RPM) + abre Trava 2 ao atingir velocidade
 *   - Right Bumper (R1) / Botão B .. Ligar Shooter NÉCTAR (2800 RPM) + abre Trava 1 ao atingir velocidade
 *   - Botão Y ...................... Ativar servos flower1 e flower2 (indexers)
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

    // Servos flower1 e flower2 (suporta CRServo e Servo posicional)
    private CRServo flower1;
    private CRServo flower2;
    private Servo flower1Pos;
    private Servo flower2Pos;

    /** Cronômetro do tempo de partida (mostrado na telemetria). */
    private final ElapsedTime tempoDePartida = new ElapsedTime();

    // ===================== ESTADO DO TELEOP =====================

    /** true quando o shooter está sendo acionado (L1 ou R1 pressionado no gamepad 2). */
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
            controlarFlowers();
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

        try {
            flower1 = hardwareMap.get(CRServo.class, "flower1");
        } catch (Exception e1) {
            try {
                flower1 = hardwareMap.get(CRServo.class, "flower");
            } catch (Exception e2) {
                try {
                    flower1Pos = hardwareMap.get(Servo.class, "flower1");
                } catch (Exception e3) {
                    try {
                        flower1Pos = hardwareMap.get(Servo.class, "flower");
                    } catch (Exception ignored) {}
                }
            }
        }

        try {
            flower2 = hardwareMap.get(CRServo.class, "flower2");
        } catch (Exception e1) {
            try {
                flower2Pos = hardwareMap.get(Servo.class, "flower2");
            } catch (Exception ignored) {}
        }

        if (!indexer.isConectado() && flower1 == null && flower2 == null && flower1Pos == null && flower2Pos == null) {
            telemetry.addData("Aviso", "indexers (flower1 / flower2) não encontrados na configuração");
        }
        if (!liberador.isTrava1Conectada()) {
            telemetry.addData("Aviso", "liberador (trava1) não encontrado na configuração");
        }
        if (!liberador.isTrava2Conectada()) {
            telemetry.addData("Aviso", "liberador (trava2) não encontrado na configuração");
        }

        // Garante que comece na posição inicial (0.6)
        definirEstadoFlowers(false);

        telemetry.addData("Status", "Inicializado");
        telemetry.update();
    }

    /**
     * Tração do Robô (Apenas Gamepad 1).
     * Analógico esquerdo move o robô (relativo à quadra) e analógico direito gira.
     * Pressionar Y (ou Options/Back) no Gamepad 1 reseta a IMU.
     */
    private void controlarChassi() {
        if (gamepad1.y || gamepad1.options || gamepad1.back) {
            chassi.zerarAngulo();
        }

        chassi.moverRelativoAQuadra(
                -gamepad1.left_stick_y,   // frente/trás
                gamepad1.left_stick_x,   // lateral
                -gamepad1.right_stick_x); // rotação
    }

    /**
     * Controla o shooter e as travas de disparo (Pólen e Néctar) - Apenas Gamepad 2.
     *
     * CONTROLES (Gamepad 2):
     * - Left Bumper (L1): Liga o shooter na velocidade do PÓLEN e abre a Trava 2.
     * - Right Bumper (R1): Liga o shooter na velocidade do NÉCTAR e abre a Trava 1.
     */
    private void controlarShooterETravas() {
        boolean acionarPolen = gamepad2.left_bumper;
        boolean acionarNectar = gamepad2.right_bumper;

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

    /** Controla os servos flower1 e flower2 quando o botão Y (Apenas Gamepad 2) é pressionado. */
    private void controlarFlowers() {
        boolean acionarY = gamepad2.y;
        definirEstadoFlowers(acionarY);
    }

    /**
     * Define o estado dos servos flower (invertido):
     * - acionado = true (botão Y pressionado no gamepad 2): posição 0.0
     * - acionado = false (repouso / início): começa em 0.6
     */
    private void definirEstadoFlowers(boolean acionado) {
        if (acionado) {
            indexer.alimentar();
            if (flower1 != null) flower1.setPower(0.0);
            if (flower2 != null) flower2.setPower(0.0);
            if (flower1Pos != null) flower1Pos.setPosition(0.0);
            if (flower2Pos != null) flower2Pos.setPosition(0.0);
        } else if (!shooterLigado) {
            indexer.parar();
            if (flower1 != null) flower1.setPower(0.6);
            if (flower2 != null) flower2.setPower(0.6);
            if (flower1Pos != null) flower1Pos.setPosition(0.6);
            if (flower2Pos != null) flower2Pos.setPosition(0.6);
        }
    }

    /** Controla o intake (Apenas Gamepad 2 via analógico Y ou triggers). */
    private void controlarIntake() {
        float leftTrigger = gamepad2.left_trigger;
        float rightTrigger = gamepad2.right_trigger;
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
        telemetry.addData("Ângulo do robô (graus)", chassi.getAnguloEmGraus());
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
        telemetry.addData("Indexers (flower1/flower2)", indexer.estaGirando() || gamepad2.y ? "ATIVOS (G2-Y)" : "REPOUSO (0.6)");
        telemetry.addData("Trava 1 (néctar)", liberador.getEstadoTrava1());
        telemetry.addData("Trava 2 (pólen)", liberador.getEstadoTrava2());
        telemetry.addData("Intake", intake.getEstadoAtual());
        telemetry.update();
    }
}
