using System;
using Com.Google.Android.Exoplayer;
using Com.Google.Android.Exoplayer.Upstream;
using Net.Butterflytv.Rtmp_client;

namespace AgqrPlayer4Tv.Components.Player
{
    public class RtmpDataSource : Java.Lang.Object, IUriDataSource
    {
        private RtmpClient _rtmpClient;
        private bool _isOpened = false;

        public RtmpDataSource()
        { }

        public RtmpDataSource(string uri)
        {
            this.Uri = uri;
        }

        public void Close()
        {
            this._rtmpClient.Close();
            this._rtmpClient = null;
            this._isOpened = false;
        }

        public long Open(DataSpec p0)
        {
            if (this._isOpened)
            {
                this.Close();
            }

            this.Uri = p0.Uri.ToString();
            this._rtmpClient = new RtmpClient();
            this._rtmpClient.Open(p0.Uri.ToString(), false);
            this._isOpened = true;
            return C.LengthUnbounded;
        }

        public int Read(byte[] p0, int p1, int p2)
        {
            if (this._rtmpClient.IsConnected() == 0) return 0;
            return this._rtmpClient.Read(p0, p1, p2);
        }

        public string Uri { get; private set; }
    }
}