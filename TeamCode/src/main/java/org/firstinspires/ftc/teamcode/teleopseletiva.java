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
import org.firstinspires.ftc.teamcode.subsystems.Vision;

@TeleOp(name = "seletiva")
public class teleopseletiva extends LinearOpMode {

    private Chassi chassi;
    private Intake intake;
    private Shooter shooter;
    private Indexer indexer;
    private Liberador liberador;
    private Vision vision;

    private CRServo flower1;
    private CRServo flower2;
    private Servo flower1Pos;
    private Servo flower2Pos;

    private final ElapsedTime tempoDePartida = new ElapsedTime();

    private boolean shooterLigado = false;
    private boolean trava1Liberada = false;
    private boolean trava2Liberada = false;

    private enum ModoTiro { POLEN, NECTAR, REVERSO, POLEN_AUTO }

    private ModoTiro modoTiro = ModoTiro.POLEN;

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
            controlarVision();
            atualizarTelemetria();
        }

        if (vision != null) {
            vision.fechar();
        }
    }

    private void inicializarSubsistemas() {
        chassi = new Chassi(hardwareMap);
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        indexer = new Indexer(hardwareMap);
        liberador = new Liberador(hardwareMap);
        vision = new Vision(hardwareMap);

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

        if (!vision.isConectado()) {
            telemetry.addData("Aviso", "visão (camera) não encontrada na configuração");
        }

        definirEstadoFlowers(true);

        telemetry.addData("Status", "Inicializado");
        telemetry.update();
    }

    private void controlarChassi() {
        chassi.moverRelativoAoRobo(
                gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x
        );
    }

    private void controlarShooterETravas() {
        boolean acionarPolen = gamepad2.left_bumper;
        boolean acionarNectar = gamepad2.right_bumper;
        boolean acionarReverso = gamepad2.dpad_up;
        boolean acionarPolenAuto = gamepad2.dpad_down;
        boolean botaoAtirar = gamepad2.a;

        if (acionarReverso) {
            modoTiro = ModoTiro.REVERSO;
            shooterLigado = true;
            shooter.ligarReverso();
            liberador.segurarTrava1();
            liberador.segurarTrava2();
            trava1Liberada = false;
            trava2Liberada = false;

        } else if (acionarPolenAuto) {
            modoTiro = ModoTiro.POLEN_AUTO;
            shooterLigado = true;
            shooter.ligarPolenAuto();

            if (!botaoAtirar) {
                trava2Liberada = false;
            } else if (shooter.estaProntoParaAtirarAuto()) {
                trava2Liberada = true;
            }

            if (trava2Liberada) {
                liberador.liberarTrava2();
            } else {
                liberador.segurarTrava2();
            }

            liberador.segurarTrava1();
            trava1Liberada = false;

        } else if (acionarPolen) {
            modoTiro = ModoTiro.POLEN;
            shooterLigado = true;
            shooter.ligarPolen();

            if (!botaoAtirar) {
                trava2Liberada = false;
            } else if (shooter.estaProntoParaAtirarPolen()) {
                trava2Liberada = true;
            }

            if (trava2Liberada) {
                liberador.liberarTrava2();
            } else {
                liberador.segurarTrava2();
            }

            liberador.segurarTrava1();
            trava1Liberada = false;

        } else if (acionarNectar) {
            modoTiro = ModoTiro.NECTAR;
            shooterLigado = true;
            shooter.ligarNectar();

            if (!botaoAtirar) {
                trava1Liberada = false;
            } else if (shooter.estaProntoParaAtirarNectar()) {
                trava1Liberada = true;
            }

            if (trava1Liberada) {
                liberador.liberarTrava1();
            } else {
                liberador.segurarTrava1();
            }

            liberador.segurarTrava2();
            trava2Liberada = false;

        } else {
            shooterLigado = false;
            shooter.parar();
            liberador.segurarTrava1();
            liberador.segurarTrava2();
            trava1Liberada = false;
            trava2Liberada = false;
        }
    }

    private void controlarFlowers() {
        boolean acionarY = gamepad2.y;
        definirEstadoFlowers(acionarY);
    }

    private void definirEstadoFlowers(boolean acionado) {
        if (acionado) {
            indexer.alimentar();

            if (flower1 != null) flower1.setPower(0.9);
            if (flower2 != null) flower2.setPower(0.9);
            if (flower1Pos != null) flower1Pos.setPosition(0.9);
            if (flower2Pos != null) flower2Pos.setPosition(0.9);

        } else if (!shooterLigado) {
            indexer.parar();

            if (flower1 != null) flower1.setPower(0);
            if (flower2 != null) flower2.setPower(0);
            if (flower1Pos != null) flower1Pos.setPosition(0);
            if (flower2Pos != null) flower2Pos.setPosition(0);
        }
    }

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

    private void controlarVision() {
        if (vision != null) {
            vision.controlarStreaming(gamepad1.dpad_down, gamepad1.dpad_up);
        }
    }

    private void atualizarTelemetria() {
        boolean veAprilTag = vision != null && vision.temAprilTag();
        String statusColmeia;
        if (shooterLigado) {
            statusColmeia = veAprilTag ? "SIM" : "NÃO";
        } else {
            statusColmeia = veAprilTag ? "SIM (Shooter Desligado)" : "NÃO";
        }

        telemetry.addData("Colmeia", statusColmeia);
        telemetry.addData("Tempo de partida", tempoDePartida.toString());
        telemetry.addData("Shooter", shooterLigado ? "LIGADO (" + modoTiro + ")" : "DESLIGADO");
        telemetry.addData("Shooter pronto (Pólen)", shooter.estaProntoParaAtirarPolen() ? "SIM" : "NÃO");
        telemetry.addData("Shooter pronto (Néctar)", shooter.estaProntoParaAtirarNectar() ? "SIM" : "NÃO");
        telemetry.addData("Shooter alvo (RPM)", shooter.getVelocidadeAlvoRPM());
        telemetry.addData("Shooter média (RPM)", shooter.getVelocidadeMediaRPM());
        telemetry.addData("Indexers (flower1/flower2)", indexer.estaGirando() || gamepad2.y ? "ATIVOS (G2-Y)" : "REPOUSO (0.7)");
        telemetry.addData("Trava 1 (néctar)", liberador.getEstadoTrava1());
        telemetry.addData("Trava 2 (pólen)", liberador.getEstadoTrava2());
        telemetry.addData("Intake", intake.getEstadoAtual());

        if (vision != null) {
            vision.atualizarTelemetria(telemetry);
        }

        telemetry.update();
    }
}
