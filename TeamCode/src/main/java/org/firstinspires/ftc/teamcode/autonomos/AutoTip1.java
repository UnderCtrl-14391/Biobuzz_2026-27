package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Liberador;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

@Autonomous (name = "AutoTip1", group = "Autonomous")
public class AutoTip1 extends AutoFunction {
    private Intake intake;
    private Shooter shooter;
    private Liberador liberador;

    @Override
    public void runOpMode() {
        HardwareMap(hardwareMap);
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        liberador = new Liberador(hardwareMap);

        waitForStart();

        if (opModeIsActive()) {

            shooter.ligarPolenAuto();
            while (opModeIsActive() && !shooter.estaProntoParaAtirarAuto()) {
            }
            sleep(2000);
            liberador.liberarTrava2();
            setFlowers(true);
            intake.coletarAutonomo();
            sleep(12200);
            setFlowers(false);

            stopResetMotors();
            autonomous(-400, -400, -400, -400, 0.5, 0.5, 0, 0, 0, 1, 0, 0, 0, 700);

            stopResetMotors();
            autonomous(-2000, 2000, -2000, 2000, 0.7, 0.7, 0, 0, 0, 1, 0, 0, 0, 2000);

            stopResetMotors();
            autonomous(-3000, -3000, -3000, -3000, 0.5, 0.5, 0, 0, 0, 1, 0, 0, 0, 5000);

            stopResetMotors();
            autonomous(-1500, 1500, -1500, 1500, 0.4, 0.4, 0, 0, 0, 1, 0, 0, 0, 1700);



        }

    }
}
