package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Subsistema do LIBERADOR.
 *
 * Dois servos que ficam SEGURANDO as peças antes do shooter e só as
 * LIBERAM quando o shooter está rápido o suficiente para atirar:
 *   - trava1: libera o NÉCTAR (botão B no teleop);
 *   - trava2: libera o PÓLEN (botão A no teleop).
 * Cada trava é controlada separadamente e tem duas posições: SEGURANDO (bloqueia a passagem) e LIBERANDO (abre a passagem).
 * Se algum servo não estiver na configuração do robô, o código continua funcionando
 * (apenas ignora os comandos daquele servo) e o teleop mostra um aviso na telemetria.
 */
public class Liberador {

    // ===================== CONSTANTES DE AJUSTE =====================

    /** Posição da trava 1 segurando o pólen, com a passagem fechada (0.0 a 1.0). */
    public static double POSICAO_SEGURAR_TRAVA1 = 1;

    /** Posição da trava 1 liberando o pólen para o shooter, com a passagem aberta (0.0 a 1.0). */
    public static double POSICAO_LIBERAR_TRAVA1 = 0;

    /** Posição da trava 2 segurando o pólen, com a passagem fechada (0.0 a 1.0). */
    public static double POSICAO_SEGURAR_TRAVA2 = 0.3;

    /** Posição da trava 2 liberando o pólen para o shooter, com a passagem aberta (0.0 a 1.0). */
    public static double POSICAO_LIBERAR_TRAVA2 = 0.6;

    /** Tempo (s) que a trava fica aberta a cada aperto do botão antes de fechar de novo. */
    public static double TEMPO_ABERTO_S = 10;

    // ===================== ESTADO =====================

    /** Possíveis estados de cada trava. */
    public enum Estado { SEGURANDO, LIBERANDO }

    private Estado estadoTrava1 = Estado.SEGURANDO;
    private Estado estadoTrava2 = Estado.SEGURANDO;

    // ===================== HARDWARE =====================

    private Servo servoTrava1;
    private Servo servoTrava2;

    /**
     * Construtor: busca os servos das travas e já os deixa segurando o pólen.
     *
     * @param hardwareMap mapa de hardware recebido do OpMode
     */
    public Liberador(HardwareMap hardwareMap) {
        // Os textos entre aspas são os nomes cadastrados na configuração do Driver Station
        servoTrava1 = buscarServo(hardwareMap, "trava1");
        servoTrava2 = buscarServo(hardwareMap, "trava2");
        segurar();
    }

    /** Busca um servo pelo nome; devolve null se ele não estiver na configuração. */
    private static Servo buscarServo(HardwareMap hardwareMap, String nome) {
        try {
            return hardwareMap.get(Servo.class, nome);
        } catch (Exception erro) {
            return null;
        }
    }

    /** Fecha as duas travas. */
    public void segurar() {
        segurarTrava1();
        segurarTrava2();
    }

    /** Fecha a trava 1 (néctar). */
    public void segurarTrava1() {
        estadoTrava1 = Estado.SEGURANDO;
        if (servoTrava1 != null) servoTrava1.setPosition(POSICAO_SEGURAR_TRAVA1);
    }

    /** Abre a trava 1 (néctar) para a peça seguir até o shooter. */
    public void liberarTrava1() {
        estadoTrava1 = Estado.LIBERANDO;
        if (servoTrava1 != null) servoTrava1.setPosition(POSICAO_LIBERAR_TRAVA1);
    }

    /** Fecha a trava 2 (pólen). */
    public void segurarTrava2() {
        estadoTrava2 = Estado.SEGURANDO;
        if (servoTrava2 != null) servoTrava2.setPosition(POSICAO_SEGURAR_TRAVA2);
    }

    /** Abre a trava 2 (pólen) para a peça seguir até o shooter. */
    public void liberarTrava2() {
        estadoTrava2 = Estado.LIBERANDO;
        if (servoTrava2 != null) servoTrava2.setPosition(POSICAO_LIBERAR_TRAVA2);
    }

    /** @return o estado atual da trava 1 (SEGURANDO ou LIBERANDO) */
    public Estado getEstadoTrava1() {
        return estadoTrava1;
    }

    /** @return o estado atual da trava 2 (SEGURANDO ou LIBERANDO) */
    public Estado getEstadoTrava2() {
        return estadoTrava2;
    }

    /** @return true se a trava 1 foi encontrada na configuração do robô */
    public boolean isTrava1Conectada() {
        return servoTrava1 != null;
    }

    /** @return true se a trava 2 foi encontrada na configuração do robô */
    public boolean isTrava2Conectada() {
        return servoTrava2 != null;
    }
}
