package it.agevoluzione.tools.android.utils.reminder;

public interface IRemind {
    String EXTRA = "rmd.xtr";
    String ACTION = "rmd.act";
    void at(long time);
    void cancel();
}
