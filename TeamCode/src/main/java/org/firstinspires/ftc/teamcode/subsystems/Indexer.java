package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Subsistema dos INDEXERS.
 *
 * Dois servos de rotação contínua (CRServo) que giram juntos e levam o pólen
 * até o liberador, que o segura até o shooter estar pronto para atirar.
 *
 * IMPORTANTE: na configuração do Driver Station, "flower" e "flower2" precisam estar
 * cadastrados como "Continuous Rotation Servo" (e não como "Servo").
 * Se os servos não forem encontrados, o código continua funcionando
 * (apenas ignora os comandos) e o teleop mostra um aviso na telemetria.
 */
public class Indexer {

    // ===================== CONSTANTES DE AJUSTE =====================

    /** Potência dos indexers ao alimentar o shooter (-1 a 1; troque o sinal se girarem ao contrário). */
    public static double POTENCIA_ALIMENTAR = 1.0;

    // ===================== ESTADO =====================

    private boolean girando = false;

    // ===================== HARDWARE =====================

    private CRServo servoIndexer1;
    private CRServo servoIndexer2;

    /** Fica false se algum servo não foi encontrado na configuração. */
    private boolean conectado = true;

    /**
     * Construtor: busca os servos dos indexers e garante que comecem parados.
     *
     * @param hardwareMap mapa de hardware recebido do OpMode
     */
    public Indexer(HardwareMap hardwareMap) {
        try {
            // Os textos entre aspas são os nomes cadastrados na configuração do Driver Station
            servoIndexer1 = hardwareMap.get(CRServo.class, "flower");
            servoIndexer2 = hardwareMap.get(CRServo.class, "flower2");

            servoIndexer1.setDirection(DcMotorSimple.Direction.FORWARD);
            servoIndexer2.setDirection(DcMotorSimple.Direction.FORWARD);
        } catch (Exception erro) {
            conectado = false;
        }
        parar();
    }

    /** Gira os indexers para levar o pólen até o shooter. */
    public void alimentar() {
        definirPotencia(POTENCIA_ALIMENTAR);
        girando = true;
    }

    /** Para os indexers. */
    public void parar() {
        definirPotencia(0.0);
        girando = false;
    }

    /**
     * Aplica a mesma potência nos dois servos (ignora os que não foram encontrados).
     *
     * @param potencia valor de -1 a 1
     */
    private void definirPotencia(double potencia) {
        if (servoIndexer1 != null) servoIndexer1.setPower(potencia);
        if (servoIndexer2 != null) servoIndexer2.setPower(potencia);
    }

    /** @return true se os indexers estão girando */
    public boolean estaGirando() {
        return girando;
    }

    /** @return true se os dois servos foram encontrados na configuração do robô */
    public boolean isConectado() {
        return conectado;
    }
}
