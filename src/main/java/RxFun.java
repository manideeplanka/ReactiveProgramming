import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.util.List;

class RxFun {
    private String value;

    private void setValue(String value) {
        this.value = value;
    }

    // just(), from() and other creation tools store the value of data when created not when subscribed

    private Observable<String> valueObservable() {
//        return Observable.just(value); //returns null

        //retrieves value on subscription, not creation
        /*return Observable.create(subscriber -> {
                    subscriber.onNext(value);
                    subscriber.onCompleted();
                }
        );*/

        //defer() creates a new Observable for each Subscriber
        return Observable.defer(() -> Observable.just(value));
    }

    Observable<List<Integer>> wow() {
        return Observable.defer(() -> Observable.just(1, 2, 3, 4, 5)
                .toList())
                .retryWhen(observable -> observable.flatMap(o -> {
                    if (o instanceof IOException) {
                        return Observable.just(null);
                    }
                    return Observable.error(o);
                }));
    }


    public static void main(String[] args) {
        RxFun instance = new RxFun();
        Observable<String> value = instance.valueObservable();
        instance.setValue("Some Value");
        value.subscribe(System.out::println);

        instance.wow().subscribe(new Subscriber<List<Integer>>() {
            @Override
            public void onCompleted() {
                System.out.println("completed");
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(List<Integer> integers) {
                System.out.print("onNext: ");
                System.out.println(integers);
            }
        });
    }
}