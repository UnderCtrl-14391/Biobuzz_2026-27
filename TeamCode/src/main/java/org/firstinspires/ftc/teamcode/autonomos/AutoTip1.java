package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.subsystems.Chassi;
import org.firstinspires.ftc.teamcode.subsystems.Indexer;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Liberador;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.teleopseletiva;

@Autonomous (name = "AutoTip1", group = "Autonomous")
public class AutoTip1 extends AutoFunction {
    private Chassi chassi;
    private Intake intake;
    private Shooter shooter;
    private Indexer indexer;
    private Liberador liberador;


    @Override
    public void runOpMode() {
        HardwareMap(hardwareMap);
        waitForStart();
            intake = new Intake(hardwareMap);
            shooter = new Shooter(hardwareMap);
            indexer = new Indexer(hardwareMap);
            liberador = new Liberador(hardwareMap);



            if (opModeIsActive()) {

                //stopResetMotors();
                //autonomous(0,0,0,0,0,0,0,0,0,1500,1500,1500, 3000);

                shooter.ligar();
                while (!shooter.estaProntoParaAtirar()) {
                }
                sleep(1000);
                liberador.liberarTrava2();
                intake.coletarAutonomo();
                sleep(4000);
                //stopResetMotors();
                //autonomous(0,0,0,0,0,0,-0.3,0,1,1500,1500,1500, 7000);

                stopResetMotors();
                autonomous(-400, -400, -400, -400, 0.5, 0.5, 0, 0, 0, 0, 0, 3200, 700);

                stopResetMotors();
                autonomous(-2000, 2000, -2000, 2000, 0.7, 0.7, 0, 0, 0, 0, 0, 0, 2000);

                stopResetMotors();
                autonomous(-3200, -3200, -3200, -3200, 0.5, 0.5, 0, 0, 0, 0, 0, 0, 5000);

                stopResetMotors();
                autonomous(-1500, 1500, -1500, 1500, 0.7, 0.7, 0, 0, 0, 0, 0, 0, 1700);

            }

        }
    }

