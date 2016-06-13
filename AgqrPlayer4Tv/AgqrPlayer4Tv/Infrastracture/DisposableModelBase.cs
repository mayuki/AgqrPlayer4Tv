using System;
using System.Collections;
using System.Collections.Generic;
using System.Reactive.Disposables;

namespace AgqrPlayer4Tv.Infrastracture
{
    public abstract class DisposableModelBase : IDisposable, ICollection<IDisposable>
    {
        protected CompositeDisposable LifetimeDisposable { get; private set; } = new CompositeDisposable();

        public virtual void Dispose()
        {
            if (this.LifetimeDisposable != null)
            {
                this.LifetimeDisposable.Dispose();
                this.LifetimeDisposable = null;
            }
        }

        #region ICollection<IDisposable> Implementation
        IEnumerator<IDisposable> IEnumerable<IDisposable>.GetEnumerator()
        {
            return LifetimeDisposable.GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return ((IEnumerable) LifetimeDisposable).GetEnumerator();
        }

        void ICollection<IDisposable>.Add(IDisposable item)
        {
            LifetimeDisposable.Add(item);
        }

        void ICollection<IDisposable>.Clear()
        {
            LifetimeDisposable.Clear();
        }

        bool ICollection<IDisposable>.Contains(IDisposable item)
        {
            return LifetimeDisposable.Contains(item);
        }

        void ICollection<IDisposable>.CopyTo(IDisposable[] array, int arrayIndex)
        {
            LifetimeDisposable.CopyTo(array, arrayIndex);
        }

        bool ICollection<IDisposable>.Remove(IDisposable item)
        {
            return LifetimeDisposable.Remove(item);
        }

        int ICollection<IDisposable>.Count
        {
            get { return ((ICollection<IDisposable>) LifetimeDisposable).Count; }
        }

        bool ICollection<IDisposable>.IsReadOnly
        {
            get { return ((ICollection<IDisposable>) LifetimeDisposable).IsReadOnly; }
        }
        #endregion
    }
}