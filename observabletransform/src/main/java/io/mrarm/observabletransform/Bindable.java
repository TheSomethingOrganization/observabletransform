package io.mrarm.observabletransform;

/**
 * This interface specifies an observable object which has been transformed by one of the methods.
 *
 * In particular it defines two methods: bind() and unbind(). The methods allow you to dispose of
 * the object correctly. If unbind() is not called then the object will stay around in memory
 * consuming both memory and CPU until the dependant objects has been disposed.
 *
 * The bind() and unbind() methods should be reference counted. That is, if bind() is called twice,
 * then for the object to actually unbind() it should also be called twice.
 * A bind() on a child object should also call bind() on all parent objects (same applies to
 * unbind())
 */
public interface Bindable {

    void bind();

    void unbind();

}
