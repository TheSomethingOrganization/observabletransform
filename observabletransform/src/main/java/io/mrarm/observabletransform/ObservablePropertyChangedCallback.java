package io.mrarm.observabletransform;

import androidx.databinding.Observable;

class ObservablePropertyChangedCallback extends Observable.OnPropertyChangedCallback {

    private TransformedObservable observable;

    ObservablePropertyChangedCallback(TransformedObservable observable) {
        this.observable = observable;
    }

    @Override
    public void onPropertyChanged(Observable sender, int propertyId) {
        observable.reapply();
    }

}
