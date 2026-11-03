import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous (name = "AutoFunction", group = "Autonomous" )
public class AutoFunction extends LinearOpMode {

    public DcMotor FrenteD, FrenteE, TrasE, TrasD, Intake, conveyor, shooter, shooter2;
    public Servo CRservo;
    public void HardwareMap(com.qualcomm.robotcore.hardware.HardwareMap HardwareMap) {
        FrenteE = HardwareMap.get(DcMotor.class, "FrenteE");
        FrenteD = HardwareMap.get(DcMotor.class, "FrenteD");
        TrasD = HardwareMap.get(DcMotor.class, "TrasD");
        TrasE = HardwareMap.get(DcMotor.class, "TrasE");
        Intake = HardwareMap.get(DcMotor.class, "Intake");
        conveyor = HardwareMap.get(DcMotor.class, "conveyor");
        shooter = HardwareMap.get(DcMotor.class, "shooter");
        shooter2 = HardwareMap.get(DcMotor.class, "shooter2");

        FrenteD.setDirection(DcMotorSimple.Direction.FORWARD);
        FrenteE.setDirection(DcMotorSimple.Direction.FORWARD);
        TrasE.setDirection(DcMotorSimple.Direction.FORWARD);
        TrasD.setDirection(DcMotorSimple.Direction.FORWARD);
        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        conveyor.setDirection(DcMotorSimple.Direction.FORWARD);
        shooter.setDirection(DcMotorSimple.Direction.FORWARD);
        shooter2.setDirection(DcMotorSimple.Direction.FORWARD);

        FrenteE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrenteD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TrasE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TrasD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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
        public void autonomous(int DFrente, int EFrente, int ETras, int DTras, double Dpower, double Epower, int Intake, int conveyor, double shooter, double shooter2) {

        FrenteD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrenteE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        TrasE.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        TrasD.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FrenteD.setTargetPosition(DFrente);
        FrenteE.setTargetPosition(EFrente);
        TrasD.setTargetPosition(DTras);
        TrasE.setTargetPosition(ETras);
        
        }




}
