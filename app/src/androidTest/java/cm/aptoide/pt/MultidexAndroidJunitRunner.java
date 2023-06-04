import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import androidx.multidex.MultiDex;
import androidx.test.runner.AndroidJUnitRunner;

public class MultidexAndroidJunitRunner extends AndroidJUnitRunner {

  @Override 
  public void onCreate(Bundle arguments) {
    MultiDex.install(getTargetContext());
    super.onCreate(arguments);
  }

  @Override 
  public Application newApplication(ClassLoader cl, String className, Context context)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    return super.newApplication(cl, MockAptoideApplication.class.getName(), context);
  }
  
  // Add any additional methods or overrides here for new features or bug fixes
  
  // Example method:
  private void exampleMethod() {
    // Add your implementation here
  }
}
