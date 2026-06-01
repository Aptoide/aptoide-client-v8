package cm.aptoide.pt.installer.obb

import android.content.Context
import android.os.Process
import cm.aptoide.pt.extensions.getProcessName

fun Context.isObbMoverProcess(): Boolean =
  getProcessName(Process.myPid()) == "$packageName:obbMoverProcess"
