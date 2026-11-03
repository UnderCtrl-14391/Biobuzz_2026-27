package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "seletiva")
public class teleopseletiva extends LinearOpMode {
    // Hardware Members
    private DcMotor frontLeftMotor, BackLeftMotor, frontRightMotor, BackRightMotor;
    private DcMotor intake, intake2;
    private DcMotorEx shooter, shooter2;
    private Servo flower1, flower2;
    private Servo trava1, trava2;
    private IMU imu;

    private final ElapsedTime runtime = new ElapsedTime();

    // Constants
    public double INTAKE_IN_POWER = 0.7;
    public double INTAKE_OUT_POWER = -0.9;
    public double INTAKE_OFF_POWER = 0.0;

    // Servo Positions for Flower
    public double FLOWER_DOWN_POS = 0.0;
    public double FLOWER_UP_POS = 1.0;
    public double FLOWER1 = 1.0;
    public double FLOWER2 = 1.0;

    // Servo Positions / States for Trava
    public double TRAVA_DOWN_POS = 0.0;
    public double TRAVA_UP_POS = 1.0;
    public double TRAVA1_POS = 0.0;
    public double TRAVA2_POS = 0.0;

    // Shooter Target Velocities (PIDF)
    public double SHOOTER_VELOCITY_RIGHT = 2000.0; // Target PIDF velocity for Right Trigger
    public double SHOOTER_VELOCITY_LEFT = 1200.0;  // Target PIDF velocity for Left Trigger

    public double SLOW_DRIVETRAIN = 0.3;

    // State Enums
    public enum FlowerModes { UP, DOWN, OFF }
    public enum TravaMode { UP, DOWN }

    private FlowerModes flowerMode = FlowerModes.UP;
    private TravaMode trava1Mode = TravaMode.DOWN;
    private TravaMode trava2Mode = TravaMode.DOWN;

    // Edge-detection tracking for buttons
    private boolean lastButtonA = false;
    private boolean lastDpadUp = false;
    private boolean lastDpadDown = false;

    // PIDF Coefficients for Shooter Motors
    public static double SHOOTER_P = 1.26;
    public static double SHOOTER_I = 0.126;
    public static double SHOOTER_D = 0.0;
    public static double SHOOTER_F = 12.6;
    public static double SHOOTER_POSITION_P = 5.0;

    @Override
    public void runOpMode() {
        // 1. HARDWARE MAPPING
        frontLeftMotor = hardwareMap.get(DcMotor.class, "leftT");
        BackLeftMotor = hardwareMap.get(DcMotor.class, "left");
        frontRightMotor = hardwareMap.get(DcMotor.class, "rightT");
        BackRightMotor = hardwareMap.get(DcMotor.class, "right");

        intake = hardwareMap.get(DcMotor.class, "intake");
        intake2 = hardwareMap.get(DcMotor.class, "intake2");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter2 = hardwareMap.get(DcMotorEx.class, "shooter2");

        try {
            flower1 = hardwareMap.get(Servo.class, "flower");
            flower2 = hardwareMap.get(Servo.class, "flower2");
        } catch (Exception e) {
            telemetry.addData("Warning", "flower / flower2 not found in hardware map");
        }

        try {
            trava1 = hardwareMap.get(Servo.class, "trava1");
            trava2 = hardwareMap.get(Servo.class, "trava2");
        } catch (Exception e) {
            telemetry.addData("Warning", "trava1 / trava2 not found in hardware map");
        }

        // 2. MOTOR DIRECTIONS
        frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        BackLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        BackRightMotor.setDirection(DcMotor.Direction.REVERSE);

        intake.setDirection(DcMotor.Direction.FORWARD);
        intake2.setDirection(DcMotor.Direction.FORWARD);
        shooter.setDirection(DcMotor.Direction.REVERSE);
        shooter2.setDirection(DcMotor.Direction.REVERSE);

        // 3. BRAKE BEHAVIOR
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // 4. APPLY PIDF TO SHOOTER AND SHOOTER2
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter.setVelocityPIDFCoefficients(SHOOTER_P, SHOOTER_I, SHOOTER_D, SHOOTER_F);
        shooter2.setVelocityPIDFCoefficients(SHOOTER_P, SHOOTER_I, SHOOTER_D, SHOOTER_F);
        shooter.setPositionPIDFCoefficients(SHOOTER_POSITION_P);
        shooter2.setPositionPIDFCoefficients(SHOOTER_POSITION_P);

        // Set initial positions for servos
        if (flower1 != null) flower1.setPosition(FLOWER1);
        if (flower2 != null) flower2.setPosition(FLOWER2);
        if (trava1 != null) trava1.setPosition(TRAVA1_POS);
        if (trava2 != null) trava2.setPosition(TRAVA2_POS);

        // 5. IMU INITIALIZATION
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // DRIVE LOGIC (Gamepad 1)
            double forward = gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double rotate = gamepad1.right_stick_x;

            if (gamepad1.right_trigger >= 0.05) {
                forward *= SLOW_DRIVETRAIN;
                strafe *= SLOW_DRIVETRAIN;
                rotate *= SLOW_DRIVETRAIN;
            }

            driveFieldRelative(forward, strafe, rotate);

            // MECHANISM LOGIC (Gamepad 2)

            // 1. Intake Logic
            if (gamepad2.left_stick_y < -0.2) {
                intake.setPower(INTAKE_IN_POWER);
                intake2.setPower(INTAKE_IN_POWER);
            } else if (gamepad2.left_stick_y > 0.2) {
                intake.setPower(INTAKE_OUT_POWER);
                intake2.setPower(INTAKE_OUT_POWER);
            } else {
                intake.setPower(INTAKE_OFF_POWER);
                intake2.setPower(INTAKE_OFF_POWER);
            }

            // 2. Shooter / Shooter2 PIDF Velocity Control (Triggers)
            if (gamepad2.right_trigger > 0.1) {
                // Velocity 1 for Right Trigger
                double targetVel = gamepad2.right_trigger * SHOOTER_VELOCITY_RIGHT;
                setShooterVelocity(targetVel);
            } else if (gamepad2.left_trigger > 0.1) {
                // Velocity 2 for Left Trigger
                double targetVel = gamepad2.left_trigger * SHOOTER_VELOCITY_LEFT;
                setShooterVelocity(targetVel);
            } else {
                setShooterVelocity(0.0);
            }

            // 3. Flower Servo Toggle Logic (Button A)
            // Press once -> DOWN (FLOWER_DOWN_POS = 0.0), Press again -> UP (FLOWER_UP_POS = 1.0)
            boolean currentButtonA = gamepad2.a;
            if (currentButtonA && !lastButtonA) {
                if (flowerMode == FlowerModes.UP) {
                    flowerMode = FlowerModes.DOWN;
                    FLOWER1 = FLOWER_DOWN_POS;
                    FLOWER2 = FLOWER_DOWN_POS;
                } else {
                    flowerMode = FlowerModes.UP;
                    FLOWER1 = FLOWER_UP_POS;
                    FLOWER2 = FLOWER_UP_POS;
                }
                if (flower1 != null) flower1.setPosition(FLOWER1);
                if (flower2 != null) flower2.setPosition(FLOWER2);
            }
            lastButtonA = currentButtonA;

            // 4. Trava 1 Mode Toggle (DPAD UP)
            boolean currentDpadUp = gamepad2.dpad_up;
            if (currentDpadUp && !lastDpadUp) {
                if (trava1Mode == TravaMode.DOWN) {
                    trava1Mode = TravaMode.UP;
                    TRAVA1_POS = TRAVA_UP_POS;
                } else {
                    trava1Mode = TravaMode.DOWN;
                    TRAVA1_POS = TRAVA_DOWN_POS;
                }
                if (trava1 != null) trava1.setPosition(TRAVA1_POS);
            }
            lastDpadUp = currentDpadUp;

            // 5. Trava 2 Mode Toggle (DPAD DOWN)
            boolean currentDpadDown = gamepad2.dpad_down;
            if (currentDpadDown && !lastDpadDown) {
                if (trava2Mode == TravaMode.DOWN) {
                    trava2Mode = TravaMode.UP;
                    TRAVA2_POS = TRAVA_UP_POS;
                } else {
                    trava2Mode = TravaMode.DOWN;
                    TRAVA2_POS = TRAVA_DOWN_POS;
                }
                if (trava2 != null) trava2.setPosition(TRAVA2_POS);
            }
            lastDpadDown = currentDpadDown;

            // Telemetry
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Shooter 1 Velocity", shooter.getVelocity());
            telemetry.addData("Shooter 2 Velocity", shooter2.getVelocity());
            telemetry.addData("Flower Mode", flowerMode);
            telemetry.addData("FLOWER1 Pos", FLOWER1);
            telemetry.addData("FLOWER2 Pos", FLOWER2);
            telemetry.addData("Trava 1 Mode", trava1Mode);
            telemetry.addData("Trava 2 Mode", trava2Mode);
            telemetry.addData("Heading", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
            telemetry.update();
        }
    }

    // Helper method for Shooter
    public void setShooterVelocity(double velocity) {
        shooter.setVelocity(velocity);
        shooter2.setVelocity(velocity);
    }

    // Mecanum Drive Math
    public void drive(double forward, double strafe, double rotate) {
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower = forward - strafe + rotate;
        double frontRightPower = forward + strafe - rotate;
        double backRightPower = forward - strafe - rotate;

        // Normalize powers so no value exceeds 1.0
        double max = Math.max(1.0, Math.abs(frontLeftPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backRightPower));

        frontLeftMotor.setPower(frontLeftPower / max);
        BackLeftMotor.setPower(backLeftPower / max);
        frontRightMotor.setPower(frontRightPower / max);
        BackRightMotor.setPower(backRightPower / max);
    }

    // Field Centric Drive Math
    public void driveFieldRelative(double forward, double strafe, double rotate) {
        double robotHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the movement vectors by the robot's heading
        double rotX = strafe * Math.cos(robotHeading) - forward * Math.sin(robotHeading);
        double rotY = strafe * Math.sin(robotHeading) + forward * Math.cos(robotHeading);

        drive(rotY, rotX, rotate);
    }
}
