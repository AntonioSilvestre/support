package it.agevoluzione.tools.android.rfidreaderhelper;

import android.content.Context;

public interface IReaderHelper {

    public void init(Context context);
    public void connect(Context context, ReaderConfigurator configurator) throws Exception;
    public void startReading() throws Exception;
    public void stopReading();
    public void disconnect(Context context);
    public void close(Context context);

}
