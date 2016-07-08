# RxJava

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}

-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}

-keep class rx.schedulers.TestScheduler {
    public <methods>;
}

-keep class rx.subjects.SubjectSubscriptionManager {
    public <methods>;
}

-keep class rx.schedulers.Schedulers {
    public static ** test();
}
