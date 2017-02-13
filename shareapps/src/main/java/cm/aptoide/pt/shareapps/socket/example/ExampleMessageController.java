package cm.aptoide.pt.shareapps.socket.example;

import cm.aptoide.pt.shareapps.socket.message.client.AptoideMessageClientController;
import java.util.Random;

/**
 * Created by neuro on 29-01-2017.
 */

public class ExampleMessageController extends AptoideMessageClientController {

  public ExampleMessageController() {
    super("/tmp/a", bytes -> new Random().nextBoolean());
  }
}
