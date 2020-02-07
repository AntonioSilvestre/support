package it.agevoluzione.tools.android.model;

public final class NotificationStatus {
    /**
     * Stato Sconosciuto
     */
    public final static int UNKNOW = -1;
    /**
     * Stato non inizializzato
     */
    public final static int NOT_SET = 0;
    /**
     * Notifica impostata
     */
    public final static int SCHEDULED = 1;
    /**
     * Notifica visulizzata
     */
    public final static int DISABLED = 2;
    /**
     * Notifica scaduta/passata
     */
    public final static int SHOWED = 3;
    /**
     * Notifica scaduta/passata
     */
    public final static int EXPIRED = 4;
    /**
     * Stato abilitata ma non impostata
     */
    public final static int SKIPPED = 6;
    /**
     * Notifica disabilitata
     */
    public final static int LOST = 7;
    /**
     * Notifica visualizzata e succesivamente chiusa
     */
    public final static int CATCH = 8;

    private  NotificationStatus(){}
}
