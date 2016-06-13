using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using AgqrPlayer4Tv.Activities;
using AgqrPlayer4Tv.Infrastracture.Extensions;
using AgqrPlayer4Tv.Model.Platform;
using Android.App;
using Android.Content.PM;
using Android.OS;
using Android.Support.V17.Leanback.App;
using Android.Support.V17.Leanback.Widget;

namespace AgqrPlayer4Tv.Components.Fragments.GuidedStep
{
    /// <summary>
    /// �ݒ��ʂ̃g�b�v�ƂȂ�GuidedStep�N���X�ł��B
    /// </summary>
    public class SettingsGuidedStepFragment : GuidedStepFragment
    {
        public override GuidanceStylist.Guidance OnCreateGuidance(Bundle savedInstanceState)
        {
            return new GuidanceStylist.Guidance("�ݒ�", "�A�v���P�[�V�����̊e��ݒ���s���܂�", "AgqrPlayer for Android TV", null);
        }

        public override void OnCreateActions(IList<GuidedAction> actions, Bundle savedInstanceState)
        {
            actions.AddAction(0, "�z�M�`��", "����̔z�M�`����ݒ肵�܂�");
            actions.AddAction(1, "�v���C���[", "����̃v���C���[��ݒ肵�܂�");
            actions.AddAction(2, "���̃A�v���P�[�V�����ɂ���", "�A�v���P�[�V�����ɂ��Ă̏���\�����܂�");
        }

        public override void OnGuidedActionClicked(GuidedAction action)
        {
            switch (action.Id)
            {
                case 0: Add(FragmentManager, new StreamingSettingGuidedStepFragment()); break;
                case 1: Add(FragmentManager, new PlayerSettingGuidedStepFragment()); break;
                case 2: Add(FragmentManager, new AboutSettingGuidedStepFragment()); break;
            }
        }
    }


    /// <summary>
    /// �ݒ�: �z�M�`���ݒ��GuidedStep�N���X�ł��B
    /// </summary>
    public class StreamingSettingGuidedStepFragment : GuidedStepFragment
    {
        public override GuidanceStylist.Guidance OnCreateGuidance(Bundle savedInstanceState)
        {
            return new GuidanceStylist.Guidance("�z�M�`��", "����̔z�M�`����ݒ肵�܂��B���̐ݒ��ExoPlayer�𗘗p���Ă���ۂɂ̂ݗL���ł�", "�ݒ�", null);
        }

        public override void OnCreateActions(IList<GuidedAction> actions, Bundle savedInstanceState)
        {
            var appPrefs = ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>();
            actions.AddCheckAction(1, "RTMP", "RTMP�`���𗘗p���܂��BPC�����̔z�M�`���Œx���������߂ł��B�ʐM���s����ɂȂ�ƃA�v���P�[�V�������N���b�V������ꍇ������܂��B", appPrefs.StreamingType.Value == StreamingType.Rtmp);
            actions.AddCheckAction(2, "HLS", "HTTP Live Streaming�`���𗘗p���܂��B�X�}�[�g�t�H�������̔z�M�`���Œx�����傫�ڂł�", appPrefs.StreamingType.Value == StreamingType.Hls);
        }

        public override void OnGuidedActionClicked(GuidedAction action)
        {
            foreach (var a in this.Actions)
            {
                a.Checked = (a == action);
            }

            var appPrefs = ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>();
            switch (action.Id)
            {
                case 1: appPrefs.StreamingType.Value = StreamingType.Rtmp; break;
                case 2: appPrefs.StreamingType.Value = StreamingType.Hls; break;
            }
            this.FragmentManager.PopBackStack();
        }
    }

    /// <summary>
    /// �ݒ�: �v���C���[�ݒ��GuidedStep�N���X�ł��B
    /// </summary>
    public class PlayerSettingGuidedStepFragment : GuidedStepFragment
    {
        public override GuidanceStylist.Guidance OnCreateGuidance(Bundle savedInstanceState)
        {
            return new GuidanceStylist.Guidance("�v���C���[", "����̃v���C���[��ݒ肵�܂�", "�ݒ�", null);
        }

        public override void OnCreateActions(IList<GuidedAction> actions, Bundle savedInstanceState)
        {
            var appPrefs = ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>();
            actions.AddCheckAction(1, "ExoPlayer", "ExoPlayer�𗘗p���܂��B���̃v���C���[�ł�RTMP�𗘗p�\�ł��B", appPrefs.PlayerType.Value == PlayerType.ExoPlayer);
            actions.AddCheckAction(2, "MediaPlayer", "Android�W����MediaPlayer�𗘗p���܂��B���̃v���C���[�ł�RTMP�𗘗p�ł��܂���B", appPrefs.PlayerType.Value == PlayerType.AndroidDefault);
            actions.AddCheckAction(3, "WebView", "WebView�𗘗p���܂��B���̃v���C���[�ł�RTMP�𗘗p�ł��܂���B", appPrefs.PlayerType.Value == PlayerType.WebView);
        }

        public override void OnGuidedActionClicked(GuidedAction action)
        {
            foreach (var a in this.Actions)
            {
                a.Checked = (a == action);
            }

            var appPrefs = ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>();
            switch (action.Id)
            {
                case 1: appPrefs.PlayerType.Value = PlayerType.ExoPlayer; break;
                case 2: appPrefs.PlayerType.Value = PlayerType.AndroidDefault; break;
                case 3: appPrefs.PlayerType.Value = PlayerType.WebView; break;
            }
            this.FragmentManager.PopBackStack();
        }
    }

    /// <summary>
    /// �ݒ�: ���̃A�v���P�[�V�����ɂ���
    /// </summary>
    public class AboutSettingGuidedStepFragment : GuidedStepFragment
    {
        public override GuidanceStylist.Guidance OnCreateGuidance(Bundle savedInstanceState)
        {
            return new GuidanceStylist.Guidance("AgqrPlayer for Android TV�ɂ���", "�A�v���P�[�V�����ɂ��Ă̏���\�����܂�", "�ݒ�", null);
        }

        public override void OnCreateActions(IList<GuidedAction> actions, Bundle savedInstanceState)
        {
            var version = typeof(MainActivity).Assembly.GetName().Version;
            var buildDateTime = new DateTime(2000, 1, 1).Add(new TimeSpan(TimeSpan.TicksPerDay * version.Build + TimeSpan.TicksPerSecond * 2 * version.Revision)); // http://stackoverflow.com/questions/1600962/displaying-the-build-date
            var packageInfo = Application.Context.PackageManager.GetPackageInfo(Application.Context.PackageName, PackageInfoFlags.MetaData);

            var index = 0;
            actions.AddInfo(++index, "�o�[�W�������", $"{packageInfo.VersionName} (VersionCode {packageInfo.VersionCode})");
            actions.AddInfo(++index, "�r���h����", buildDateTime.ToString());
            actions.AddInfo(++index, "�f�o�C�X", $"{Build.Manufacturer} {Build.Model}");
            actions.AddInfo(++index, "Android OS", $"{Build.VERSION.Release} (API {Build.VERSION.Sdk})");
        }
    }
}