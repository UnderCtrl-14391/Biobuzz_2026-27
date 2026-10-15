package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "Decoded TeleOp Fixed")
public class chassis extends LinearOpMode {

    // ===== MOTORES DA TRAÇÃO =====

    private DcMotor frontLeftMotor;
    private DcMotor BackLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor BackRightMotor;

    private IMU imu;

    @Override
    public void runOpMode() {

        // ===== MAPEAMENTO DOS MOTORES =====

        frontLeftMotor = hardwareMap.get(DcMotor.class, "leftT");
        BackLeftMotor = hardwareMap.get(DcMotor.class, "left");
        frontRightMotor = hardwareMap.get(DcMotor.class, "rightT");
        BackRightMotor = hardwareMap.get(DcMotor.class, "right");

        // ===== DIREÇÃO DOS MOTORES =====

        frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        BackLeftMotor.setDirection(DcMotor.Direction.FORWARD);

        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotor.Direction.REVERSE);

        // ===== IMU =====

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD;

        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.UP;

        RevHubOrientationOnRobot orientationOnRobot =
                new RevHubOrientationOnRobot(logoDirection, usbDirection);

        imu.initialize(new IMU.Parameters(orientationOnRobot));

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // ===== JOYSTICKS =====

            double forward = -gamepad1.left_stick_y;
            double strafe = -gamepad1.left_stick_x;
            double rotate = -gamepad1.right_stick_x;

            // ===== FIELD CENTRIC =====

            driveFieldRelative(forward, strafe, rotate);

            // ===== RESET IMU =====

            if (gamepad1.startWasPressed()) {
                imu.resetYaw();
            }

            telemetry.addData(
                    "Heading",
                    imu.getRobotYawPitchRollAngles()
                            .getYaw(AngleUnit.DEGREES)
            );

            telemetry.update();
        }
    }

    // ===== TRAÇÃO MECANUM =====

    public void drive(double forward, double strafe, double rotate) {

        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower = forward + strafe - rotate;

        double maxPower = Math.max(
                1.0,
                Math.max(
                        Math.abs(frontLeftPower),
                        Math.max(
                                Math.abs(backLeftPower),
                                Math.max(
                                        Math.abs(frontRightPower),
                                        Math.abs(backRightPower)
                                )
                        )
                )
        );

        frontLeftMotor.setPower(frontLeftPower / maxPower);
        BackLeftMotor.setPower(backLeftPower / maxPower);
        frontRightMotor.setPower(frontRightPower / maxPower);
        BackRightMotor.setPower(backRightPower / maxPower);
    }

    // ===== FIELD CENTRIC =====

    public void driveFieldRelative(
            double forward,
            double strafe,
            double rotate) {

        double heading = imu.getRobotYawPitchRollAngles()
                .getYaw(AngleUnit.RADIANS);

        double rotStrafe = strafe * Math.cos(-heading)
                - forward * Math.sin(-heading);

        double rotForward = strafe * Math.sin(-heading)
                + forward * Math.cos(-heading);

        drive(rotForward, rotStrafe, rotate);
    }
}