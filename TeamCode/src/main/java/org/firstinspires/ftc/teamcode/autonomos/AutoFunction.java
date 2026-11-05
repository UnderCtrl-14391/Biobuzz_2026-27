package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous (name = "AutoFunction", group = "Autonomous" )
public class AutoFunction extends LinearOpMode {

    private final ElapsedTime runtime = new ElapsedTime();
    public DcMotor FrenteD, FrenteE, TrasE, TrasD, Intake;
    public Servo servo, servo2, servo3, servo4;
    public DcMotorEx Shooter, Shooter2;

    public void HardwareMap(com.qualcomm.robotcore.hardware.HardwareMap HardwareMap) {
        FrenteE = HardwareMap.get(DcMotor.class, "FrenteE");
        FrenteD = HardwareMap.get(DcMotor.class, "FrenteD");
        TrasD = HardwareMap.get(DcMotor.class, "TrasD");
        TrasE = HardwareMap.get(DcMotor.class, "TrasE");
        Intake = HardwareMap.get(DcMotor.class, "Intake");
        Shooter = HardwareMap.get(DcMotorEx.class, "shooter");
        Shooter2 = HardwareMap.get(DcMotorEx.class, "shooter2");

        FrenteD.setDirection(DcMotorSimple.Direction.FORWARD);
        FrenteE.setDirection(DcMotorSimple.Direction.FORWARD);
        TrasE.setDirection(DcMotorSimple.Direction.FORWARD);
        TrasD.setDirection(DcMotorSimple.Direction.FORWARD);
        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        Shooter.setDirection(DcMotorSimple.Direction.FORWARD);
        Shooter2.setDirection(DcMotorSimple.Direction.FORWARD);

        FrenteE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrenteD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TrasE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TrasD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Shooter2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

        public void stopMotors() {
            FrenteD.setPower(0);
            FrenteE.setPower(0);
            TrasD.setPower(0);
            TrasE.setPower(0);
        }
        public void stopResetMotors() {
            TrasD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            TrasE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            FrenteE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            FrenteD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        }
        public void autonomous(int DFrente, int EFrente, int ETras, int DTras, double Dpower, double Epower, double intake,int Servo,int Servo2, int Servo3, int Servo4, double shooter, double shooter2, int Runtime) {

        servo.setPosition(Servo);
        servo2.setPosition(Servo2);
        servo3.setPosition(Servo3);
        servo4.setPosition(Servo4);

        FrenteD.setPower(Dpower);
        TrasD.setPower(Dpower);
        FrenteE.setPower(Epower);
        TrasE.setPower(Epower);
        Intake.setPower(intake);

        FrenteE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FrenteD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        TrasE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        TrasD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        FrenteD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrenteE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        TrasE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        TrasD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FrenteD.setTargetPosition(DFrente);
        FrenteE.setTargetPosition(EFrente);
        TrasD.setTargetPosition(DTras);
        TrasE.setTargetPosition(ETras);

        FrenteE.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrenteD.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        TrasD.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        TrasE.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            double ticksShooter =
                    /*6000 * */shooter * Shooter.getMotorType().getTicksPerRev() / 60.0;
            double ticksShooter2 =
                    /*6000 * */shooter2 * Shooter2.getMotorType().getTicksPerRev() / 60.0;
            Shooter.setVelocity(ticksShooter);
            Shooter2.setVelocity(ticksShooter2);

            while (opModeIsActive() && runtime.seconds() < Runtime && (FrenteD.isBusy() || TrasD.isBusy() || FrenteE.isBusy() || TrasE.isBusy())) {
                telemetry.addData("FrenteD", FrenteD.getCurrentPosition());
                telemetry.update();

            }

        }
        public void runOpMode() {
            HardwareMap(hardwareMap);
            waitForStart();
        }

}
