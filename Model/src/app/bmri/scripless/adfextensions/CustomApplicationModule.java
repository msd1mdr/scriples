package app.bmri.scripless.adfextensions;

import oracle.jbo.ApplicationModule;

public interface CustomApplicationModule extends ApplicationModule{
  void callProcWithNoArgs();
}
