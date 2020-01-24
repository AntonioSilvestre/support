package it.agevoluzione.tools.android.rfidreaderhelper;

import androidx.annotation.NonNull;

public interface Configurator<E> {
    void configure(@NonNull E toConfigure) throws Exception;
}
