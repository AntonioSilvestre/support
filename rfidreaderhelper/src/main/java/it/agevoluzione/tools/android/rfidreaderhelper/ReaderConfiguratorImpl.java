package it.agevoluzione.tools.android.rfidreaderhelper;

import androidx.annotation.NonNull;

import com.thingmagic.Reader;

public class ReaderConfiguratorImpl implements ReaderConfigurator {

    private int[] antennas;
    private Reader.Region region;

    public ReaderConfiguratorImpl() {
        this(Reader.Region.EU3, 1);
    }

    public ReaderConfiguratorImpl(Reader.Region region, int... antennas) {
        this.antennas = antennas;
        this.region = region;
    }

    @Override
    public void configure(@NonNull Reader toConfigure) throws Exception {
        ReaderUtils.setAntennas(toConfigure, 1);
        ReaderUtils.setRegion(toConfigure, Reader.Region.EU3);
    }
}
