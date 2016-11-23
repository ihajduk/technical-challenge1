package step2;

import org.jetbrains.annotations.Nullable;
import rx.Observable;
import step1.model.Node;

import java.util.Stack;

/**
 * Created by iwha on 11/7/2016.
 */
class TreeOfFiles {

//    public static <T> Observable<T> convert(@Nullable Node<T> root) {
//        BehaviorSubject<Node<T>> subject = BehaviorSubject.create(root);
//        subject.flatMap(node -> Observable.from(node.getChildren())).subscribe(node -> subject.onNext(node));
//        return subject.asObservable().map(node -> node.getPayload());
//    }

    static <R> Observable<R> createConvert(@Nullable Node<R> root) {
        if(root==null){
            return Observable.empty();
        }

        Stack<Node<R>> remainingChildren = new Stack<>();
        remainingChildren.addAll(root.getChildren());
        return Observable.create(observer -> {
            while(!remainingChildren.isEmpty()){
                Node<R> node = remainingChildren.pop();
                remainingChildren.addAll(node.getChildren());
                observer.onNext(node.getPayload());
            }
        });
    }
}
