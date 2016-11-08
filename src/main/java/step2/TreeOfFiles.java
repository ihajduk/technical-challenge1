package step2;

import rx.Observable;

/**
 * Created by iwha on 11/7/2016.
 */
class TreeOfFiles {
    public static void main(String[] names) {
        hello("World", "Kasia", "Tomek");
    }

    private static void hello(String... names) {
        Observable.from(names).take(2).subscribe(s -> System.out.println("Hello " + s + "!"));
    }
}
