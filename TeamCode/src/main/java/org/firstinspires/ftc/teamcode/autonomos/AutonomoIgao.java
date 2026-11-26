package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous (group = "Autonomous", name = "AutonomoIgao")
public class AutonomoIgao extends LinearOpMode {

    private final ElapsedTime runtime = new ElapsedTime();

    public DcMotor FrenteD, FrenteE, TrasE, TrasD, Intake;

    public DcMotorEx Shooter1,Shooter2;

    public Servo servo1, servo2;

    public void HardwareMap(com.qualcomm.robotcore.hardware.HardwareMap hardwareMap) {
        FrenteD = hardwareMap.get(DcMotor.class, "direita");
        FrenteE = hardwareMap.get(DcMotor.class, "esquerda");
        TrasD = hardwareMap.get(DcMotor.class, "trasD");
        TrasE = hardwareMap.get(DcMotor.class, "trasE");
        Shooter1 = hardwareMap.get(DcMotorEx.class, "shooter1");
        Shooter2 = hardwareMap.get(DcMotorEx.class, "shooter2");
        Intake = hardwareMap.get(DcMotor.class, "intake");

        FrenteD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FrenteE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        TrasE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        TrasD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        FrenteD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrenteE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TrasD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TrasE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FrenteD.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrenteE.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        TrasE.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        TrasD.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        FrenteE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrenteD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        TrasE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        TrasD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FrenteE.setDirection(DcMotorSimple.Direction.FORWARD);
        FrenteD.setDirection(DcMotorSimple.Direction.FORWARD);
        TrasD.setDirection(DcMotorSimple.Direction.FORWARD);
        TrasE.setDirection(DcMotorSimple.Direction.FORWARD);
        Intake.setDirection(DcMotorSimple.Direction.FORWARD);

    }

    public void autonomous(int Dfrente, int Efrente, int trasD, int trasE,double Dpower, double Epower, double intake, double shooter1, double shooter2) {

        Intake.setPower(intake);
        FrenteE.setPower(Epower);
        TrasE.setPower(Epower);
        FrenteD.setPower(Dpower);
        TrasD.setPower(Dpower);

        FrenteD.setTargetPosition(Dfrente);
        FrenteE.setTargetPosition(Efrente);
        TrasD.setTargetPosition(trasD);
        TrasE.setTargetPosition(trasE);
    }

    public void stopMotors() {
        FrenteD.setPower(0);
        FrenteE.setPower(0);
        TrasD.setPower(0);
        TrasE.setPower(0);
    }

    public void stopResetMotors() {
        FrenteD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrenteE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        TrasD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        TrasE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void runOpMode() {
        HardwareMap(hardwareMap);
        waitForStart();
    }

}
