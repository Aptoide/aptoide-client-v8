package cm.aptoide.pt.view;

/**
 * Created by jose_messejana on 10-11-2017.
 */

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Scope @Documented @Retention(RUNTIME) public @interface ApplicationTestScope {
}
