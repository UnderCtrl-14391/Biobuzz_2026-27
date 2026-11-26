package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Teste Shooter e Intake", group = "Test")
public class testeshooterintake extends LinearOpMode {

    private DcMotorEx shooter1, shooter2;
    private DcMotor intake;

    // Velocidade alvo em RPM para os 2 motores do shooter usando PID (RUN_USING_ENCODER)
    public static double VELOCIDADE_ALVO_RPM = 2600.0;
    public static double TICKS_POR_VOLTA = 28.0;

    @Override
    public void runOpMode() {
        // Inicialização dos motores (2 motores no shooter e 1 no intake)
        shooter1 = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter2 = hardwareMap.get(DcMotorEx.class, "shooter2");
        intake = hardwareMap.get(DcMotor.class, "intake");

        shooter1.setDirection(DcMotor.Direction.FORWARD);
        shooter2.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);

        // Ativa o controle PID por encoder nos 2 motores do shooter
        shooter1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooter1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooter2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Pronto para iniciar (Shooter liga automático)");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // O shooter liga automaticamente assim que a partida começa (sem precisar apertar botões)
            double ticksPorSegundo = VELOCIDADE_ALVO_RPM * TICKS_POR_VOLTA / 60.0;
            shooter1.setVelocity(ticksPorSegundo);
            shooter2.setVelocity(ticksPorSegundo);

            // Controle do Intake (apenas pelos gatilhos do gamepad1)
            if (gamepad1.left_trigger > 0.1f) {
                intake.setPower(-1.0); // Coletar
            } else if (gamepad1.right_trigger > 0.1f) {
                intake.setPower(1.0);  // Expelir
            } else {
                intake.setPower(0.0);
            }



            // Telemetria para acompanhar a velocidade dos 2 motores do shooter, a média e o intake
            double rpm1 = Math.abs(shooter1.getVelocity() * 60.0 / TICKS_POR_VOLTA);
            double rpm2 = Math.abs(shooter2.getVelocity() * 60.0 / TICKS_POR_VOLTA);
            double rpmMedia = (rpm1 + rpm2) / 2.0;

            telemetry.addData("Shooter Alvo (RPM)", VELOCIDADE_ALVO_RPM);
            telemetry.addData("Shooter 1 Real (RPM)", rpm1);
            telemetry.addData("Shooter 2 Real (RPM)", rpm2);
            telemetry.addData("Shooter Média (RPM)", rpmMedia);
            telemetry.addData("Intake Potência", intake.getPower());
            telemetry.update();
        }

        // Para os motores ao encerrar o OpMode
        shooter1.setPower(0);
        shooter2.setPower(0);
        intake.setPower(0);
    }
}
