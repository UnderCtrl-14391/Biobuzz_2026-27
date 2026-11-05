
package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Subsistema do CHASSI.
 *
 * Chassi mecanum de 4 motores, controlado de forma "robot-centric" (relativa ao robô):
 * empurrar o analógico para frente sempre leva o robô para a direção em que ele está apontando,
 * independente de para onde o robô está virado.
 */
public class Chassi {

    // ===================== CONSTANTES DE AJUSTE =====================

    /** Multiplicador aplicado a todos os movimentos quando o modo lento está ativo (0.3 = 30% da velocidade). */
    public static double MULTIPLICADOR_MODO_LENTO = 0.3;

    // ===================== HARDWARE =====================

    private final DcMotor motorFrenteEsquerda;
    private final DcMotor motorTrasEsquerda;
    private final DcMotor motorFrenteDireita;
    private final DcMotor motorTrasDireita;

    /**
     * Construtor: busca os motores na configuração do robô e prepara tudo para uso.
     *
     * @param hardwareMap mapa de hardware recebido do OpMode
     */
    public Chassi(HardwareMap hardwareMap) {

        // Os textos entre aspas são os nomes cadastrados na configuração do Driver Station
        // ("T" no final = Trás)
        motorFrenteEsquerda = hardwareMap.get(DcMotor.class, "esquerda");
        motorTrasEsquerda = hardwareMap.get(DcMotor.class, "esquerdaTras");
        motorFrenteDireita = hardwareMap.get(DcMotor.class, "direita");
        motorTrasDireita = hardwareMap.get(DcMotor.class, "direitaTras");

        // Sentido de rotação de cada motor (depende de como foram montados no robô)
        motorFrenteEsquerda.setDirection(DcMotor.Direction.FORWARD);
        motorTrasEsquerda.setDirection(DcMotor.Direction.REVERSE);
        motorFrenteDireita.setDirection(DcMotor.Direction.REVERSE);
        motorTrasDireita.setDirection(DcMotor.Direction.REVERSE);
    }

    /**
     * Move o robô relativo a ele mesmo, com opção de modo lento.
     *
     * @param frente    movimento para frente/trás (-1 a 1)
     * @param lateral   movimento para os lados (-1 a 1)
     * @param rotacao   giro no próprio eixo (-1 a 1)
     * @param modoLento se true, multiplica todos os movimentos por MULTIPLICADOR_MODO_LENTO
     */
    public void moverRelativoAoRobo(double frente, double lateral, double rotacao, boolean modoLento) {
        if (modoLento) {
            frente *= MULTIPLICADOR_MODO_LENTO;
            lateral *= MULTIPLICADOR_MODO_LENTO;
            rotacao *= MULTIPLICADOR_MODO_LENTO;
        }

        moverRelativoAoRobo(frente, lateral, rotacao);
    }

    /**
     * Move o robô relativo a ele mesmo (frente = para onde o robô está apontando).
     * Calcula a potência de cada roda mecanum e normaliza para nenhuma passar de 1.0.
     *
     * @param frente  movimento para frente/trás (-1 a 1)
     * @param lateral movimento para os lados (-1 a 1)
     * @param rotacao giro no próprio eixo (-1 a 1)
     */
    public void moverRelativoAoRobo(double frente, double lateral, double rotacao) {

        // Fórmula do mecanum convencional
        double potenciaFrenteEsquerda = frente + lateral + rotacao;
        double potenciaTrasEsquerda = frente - lateral + rotacao;
        double potenciaFrenteDireita = frente - lateral - rotacao;
        double potenciaTrasDireita = frente + lateral - rotacao;

        // Se alguma potência passar de 1.0, divide todas pelo maior valor (mantém a proporção)
        double maiorPotencia = Math.max(1.0, Math.abs(potenciaFrenteEsquerda));
        maiorPotencia = Math.max(maiorPotencia, Math.abs(potenciaTrasEsquerda));
        maiorPotencia = Math.max(maiorPotencia, Math.abs(potenciaFrenteDireita));
        maiorPotencia = Math.max(maiorPotencia, Math.abs(potenciaTrasDireita));

        motorFrenteEsquerda.setPower(potenciaFrenteEsquerda / maiorPotencia);
        motorTrasEsquerda.setPower(potenciaTrasEsquerda / maiorPotencia);
        motorFrenteDireita.setPower(potenciaFrenteDireita / maiorPotencia);
        motorTrasDireita.setPower(potenciaTrasDireita / maiorPotencia);
    }

    /** Para todos os motores do chassi. */
    public void parar() {
        moverRelativoAoRobo(0, 0, 0);
    }
}
