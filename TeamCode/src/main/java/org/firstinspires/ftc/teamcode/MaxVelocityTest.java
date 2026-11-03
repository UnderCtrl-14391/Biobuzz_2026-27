package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "MaxVelocityTest", group = "Test")
public class MaxVelocityTest extends LinearOpMode {
    private DcMotorEx motor;
    private double currentVelocity;
    private double maxVelocity = 0.0;

    @Override
    public void runOpMode() {
        motor = hardwareMap.get(DcMotorEx.class, "shooter");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            motor.setPower(1.0);
            currentVelocity = motor.getVelocity();

            if (currentVelocity > maxVelocity) {
                maxVelocity = currentVelocity;
            }

            telemetry.addData("Current Velocity", currentVelocity);
            telemetry.addData("Maximum Velocity", maxVelocity);
            telemetry.update();
        }
    }
}
