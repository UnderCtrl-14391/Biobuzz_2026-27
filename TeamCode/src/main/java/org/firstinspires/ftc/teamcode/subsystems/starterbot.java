package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "equipecaos")
public class starterbot extends LinearOpMode {
    // Hardware Members
    private DcMotor frontLeftMotor;
    private DcMotor backLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backRightMotor;
    private IMU imu;
    private final ElapsedTime runtime = new ElapsedTime();
    public double SLOW_DRIVETRAIN = 0.3;

    @Override
    public void runOpMode() {
        // 1. HARDWARE MAPPING
        frontLeftMotor = hardwareMap.get(DcMotor.class, "esquerda");
        backLeftMotor = hardwareMap.get(DcMotor.class, "esquerdaTras");
        frontRightMotor = hardwareMap.get(DcMotor.class, "direita");
        backRightMotor = hardwareMap.get(DcMotor.class, "direitaTras");

        // 2. MOTOR DIRECTIONS
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE); //DIREITATRAS
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD); //ESQUERDAFRENTE

        // 3. NO WHEEL PIDF (OPEN-LOOP POWER CONTROL)
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // 4. IMU INITIALIZATION
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // Reset IMU heading when Options or A button is pressed
            if (gamepad1.options || gamepad1.a) {
                imu.resetYaw();
            }

            // Apply slow mode multiplier if right trigger is pressed
            double multiplier = (gamepad1.right_trigger >= 0.05) ? SLOW_DRIVETRAIN : 1.0;
            double forward = -gamepad1.left_stick_y * multiplier; // Inverted: pushing stick UP yields positive forward velocity
            double strafe = gamepad1.left_stick_x * multiplier;
            double rotate = gamepad1.right_stick_x * multiplier;

            // Drive relative to the field orientation using IMU heading (no wheel PIDF)
            driveFieldRelative(forward, strafe, rotate);

            // Telemetry
            telemetry.addData("Status", "Run Time: " + runtime);
            telemetry.addData("Heading (Degrees)", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
            telemetry.update();
        }
    }

    /**
     * Standard Mecanum Drive (Robot-Centric).
     * Calculates open-loop motor powers from forward, strafe, and rotation velocities.
     */
    public void drive(double forward, double strafe, double rotate) {
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower = forward + strafe - rotate;

        // Normalize powers so no motor exceeds 1.0
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)),
                Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))
        ));

        frontLeftMotor.setPower(frontLeftPower / max);
        backLeftMotor.setPower(backLeftPower / max);
        frontRightMotor.setPower(frontRightPower / max);
        backRightMotor.setPower(backRightPower / max);
    }

    /**
     * Field-Centric Mecanum Drive.
     * Uses the IMU heading to calculate field-relative velocity vectors, transforming
     * field-oriented driver controls into robot wheel powers without wheel encoder PIDF.
     */
    public void driveFieldRelative(double forward, double strafe, double rotate) {
        double robotHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate movement velocity vector by robot's IMU heading
        double rotX = strafe * Math.cos(robotHeading) - forward * Math.sin(robotHeading);
        double rotY = strafe * Math.sin(robotHeading) + forward * Math.cos(robotHeading);

        drive(rotY, rotX, rotate);
    }
}
