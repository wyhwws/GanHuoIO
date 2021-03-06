package ren.solid.ganhuoio.module.mine;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ren.solid.ganhuoio.R;
import ren.solid.ganhuoio.common.activity.SubActivity;
import ren.solid.ganhuoio.common.event.LoginEvent;
import ren.solid.ganhuoio.module.about.AboutActivity;
import ren.solid.ganhuoio.utils.AppUtils;
import ren.solid.ganhuoio.utils.AuthorityUtils;
import ren.solid.library.SettingCenter;
import ren.solid.library.fragment.base.BaseFragment;
import ren.solid.library.imageloader.ImageLoader;
import ren.solid.library.rx.RxBus;
import ren.solid.library.utils.StringStyleUtils;
import ren.solid.library.utils.ToastUtils;
import rx.functions.Action1;

/**
 * Created by _SOLID
 * Date:2017/3/14
 * Time:11:33
 * Desc:
 */

public class MineFragment extends BaseFragment {

    private TextView tv_username;
    private ImageView iv_avatar;
    private TextView tv_clear_cache;

    @Override
    protected void init() {
        RxBus.getInstance()
                .toObserverable(LoginEvent.class)
                .subscribe(new Action1<LoginEvent>() {
                    @Override
                    public void call(LoginEvent loginEvent) {
                        setUserInfo();
                    }
                });
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void setUpView() {

        iv_avatar = $(R.id.iv_avatar);
        tv_username = $(R.id.tv_username);
        tv_clear_cache = $(R.id.tv_clear_cache);
        $(R.id.tv_my_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AuthorityUtils.isLogin()) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                } else {
                    SubActivity.start(getContext(), getString(R.string.mine_collect), SubActivity.TYPE_COLLECT);
                }
            }
        });
        tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AuthorityUtils.isLogin()) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
            }
        });
        $(R.id.tv_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutActivity.class));
            }
        });
        $(R.id.tv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.logOut(getContext());
            }
        });
        tv_clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.clearCache(getContext(), new SettingCenter.ClearCacheListener() {
                    @Override
                    public void onResult() {
                        ToastUtils.getInstance().showToast("清理成功");
                        tv_clear_cache.setText(getString(R.string.mine_cache_clear));
                    }
                });
            }
        });
    }

    @Override
    protected void setUpData() {
        setUserInfo();
        refresh();
    }

    @Override
    public void refresh() {
        SettingCenter.countDirSizeTask(new SettingCenter.CountDirSizeListener() {
            @Override
            public void onResult(long result) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(getString(R.string.mine_cache_clear) + "\n");
                builder.append(StringStyleUtils.format(getContext(), "(" + SettingCenter.formatFileSize(result) + ")", R.style.ByTextAppearance));
                tv_clear_cache.setText(builder);
            }
        });
    }

    private void setUserInfo() {
        if (AuthorityUtils.isLogin()) {
            tv_username.setText(AuthorityUtils.getUserName());
            ImageLoader.displayImage(iv_avatar, AuthorityUtils.getAvatar());
        } else {
            tv_username.setText("点我登录");
            iv_avatar.setImageDrawable(new ColorDrawable(ContextCompat.getColor(getContext(),
                    R.color.colorPrimaryDark)));
        }
    }
}
