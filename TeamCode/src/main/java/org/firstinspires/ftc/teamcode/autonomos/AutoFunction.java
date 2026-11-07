package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous (name = "AutoFunction", group = "Autonomous" )
public class AutoFunction extends LinearOpMode {

    private final ElapsedTime runtime = new ElapsedTime();
    public DcMotor FrenteD, FrenteE, TrasE, TrasD, Intake;
    public Servo trava1, trava2;
    public DcMotorEx Shooter, Shooter2, Shooter3;



    public void HardwareMap(com.qualcomm.robotcore.hardware.HardwareMap HardwareMap) {
        FrenteE = HardwareMap.get(DcMotor.class, "esquerda");
        FrenteD = HardwareMap.get(DcMotor.class, "direita");
        TrasD = HardwareMap.get(DcMotor.class, "direitaTras");
        TrasE = HardwareMap.get(DcMotor.class, "esquerdaTras");
        Intake = HardwareMap.get(DcMotor.class, "intake");
        Shooter = HardwareMap.get(DcMotorEx.class, "shooter");
        Shooter2 = HardwareMap.get(DcMotorEx.class, "shooter2");
        Shooter3 = HardwareMap.get(DcMotorEx.class, "shooter3");


        try {
            trava1 = HardwareMap.get(Servo.class, "trava1");
        } catch (Exception ignored) {
            trava1 = null;
        }

        try {
            trava2 = HardwareMap.get(Servo.class, "trava2");
        } catch (Exception ignored) {
            trava2 = null;
        }

        FrenteE.setDirection(DcMotorSimple.Direction.REVERSE);
        FrenteD.setDirection(DcMotorSimple.Direction.FORWARD);
        TrasE.setDirection(DcMotorSimple.Direction.REVERSE);
        TrasD.setDirection(DcMotorSimple.Direction.FORWARD);
        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        Shooter.setDirection(DcMotorSimple.Direction.FORWARD);
        Shooter2.setDirection(DcMotorSimple.Direction.FORWARD);
        Shooter3.setDirection(DcMotorSimple.Direction.REVERSE);

        FrenteE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrenteD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TrasE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TrasD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Shooter2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Shooter3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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

    public void autonomous(int DFrente, int EFrente, int ETras, int DTras, double Dpower, double Epower, double intake, double posTrava1, double posTrava2, double shooter, double shooter2, double shooter3, int Runtime) {
        runtime.reset();

        if (trava1 != null) trava1.setPosition(posTrava1);
        if (trava2 != null) trava2.setPosition(posTrava2);

        boolean temAlvoMovimento = (DFrente != 0 || EFrente != 0 || ETras != 0 || DTras != 0);

        if (temAlvoMovimento) {
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

            FrenteD.setPower(Dpower);
            TrasD.setPower(Dpower);
            FrenteE.setPower(Epower);
            TrasE.setPower(Epower);
        } else {
            stopMotors();
        }

        Intake.setPower(intake);

        double ticksShooter = shooter * Shooter.getMotorType().getTicksPerRev() / 60.0;
        double ticksShooter2 = shooter2 * Shooter2.getMotorType().getTicksPerRev() / 60.0;
        double ticksShooter3 = shooter3 * Shooter3.getMotorType().getTicksPerRev() / 60.0;

        Shooter.setVelocity(ticksShooter);
        Shooter2.setVelocity(ticksShooter2);
        Shooter3.setVelocity(ticksShooter3);

        while (opModeIsActive() && runtime.milliseconds() < Runtime) {
            if (temAlvoMovimento && (!FrenteD.isBusy() && !TrasD.isBusy() && !FrenteE.isBusy() && !TrasE.isBusy())) {
                break;
            }
            telemetry.addData("FrenteD", FrenteD.getCurrentPosition());
            telemetry.addData("FrenteE", FrenteE.getCurrentPosition());
            telemetry.addData("TrasD", TrasD.getCurrentPosition());
            telemetry.addData("TrasE", TrasE.getCurrentPosition());
            telemetry.update();
        }
    }

    public void runOpMode() {
        HardwareMap(hardwareMap);
        waitForStart();
    }
}
