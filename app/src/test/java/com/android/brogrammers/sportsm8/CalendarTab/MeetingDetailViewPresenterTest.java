package com.android.brogrammers.sportsm8.CalendarTab;

import android.support.annotation.NonNull;

import com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP.MeetingDetailView;
import com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP.MeetingDetailViewPresenter;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

public class MeetingDetailViewPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    MeetingDetailView view;
    @Mock
    UserRepository userRepository;

    private MeetingDetailViewPresenter presenter;
    private final List<UserInfo> MANY_USERS = Arrays.asList(new UserInfo("email@email.com"), new UserInfo("email@email.com"), new UserInfo("email@email.com"));
    private final Map<String, String> membersMap = new HashMap<>();
    private final Meeting meeting = new Meeting(404, 3);

    @Before
    public void setUp() {
        presenter = new MeetingDetailViewPresenter(view, userRepository, Schedulers.trampoline());
        for (int i = 0; i < 3; i++) {
            membersMap.put("member" + i, "email@email.com");
        }
        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(@NonNull Scheduler scheduler) throws Exception {
                return Schedulers.trampoline();
            }
        });
    }

    @Test
    public void shouldPassMembersToView() {
        //given
        Mockito.when(userRepository.getUsers(meeting.MeetingID)).thenReturn(Single.just(MANY_USERS));
        //when
        presenter.loadMembers(meeting);
        //then
        Mockito.verify(view).displayMembers(MANY_USERS);
    }

    @Test
    public void shouldSetUpProgressBar() {
        Mockito.when(userRepository.getUsers(meeting.MeetingID)).thenReturn(Single.just(MANY_USERS));

        presenter.loadMembers(meeting);

        Mockito.verify(view).setUpprogressBar(0, 3);
    }

    @Test
    public void shouldHandleNoMembersPassed() {
        Mockito.when(userRepository.getUsers(meeting.MeetingID)).thenReturn(Single.<List<UserInfo>>just(Collections.EMPTY_LIST));
        presenter.loadMembers(meeting);
        Mockito.verify(view).displayNoMembers();
    }

    @Test
    public void shouldAddMembers() throws Exception {
        Mockito.when(userRepository.addUsersToMeeting(meeting.MeetingID,membersMap)).thenReturn(Single.just(ResponseBody.create(MediaType.parse("JSON"),"success")));
        presenter.addMembers(meeting,MANY_USERS);
        Mockito.verify(view).updateMemberList();
    }

//
//    @Test
//    public void shouldDisplayErrorWhenNoUsersAdded() throws Exception {
//        Mockito.when(userRepository.addUsersToMeeting(meeting.MeetingID,membersMap)).thenReturn(Single.<ResponseBody>error(new Throwable("boom")));
//        presenter.addMembers(meeting,MANY_USERS);
//        Mockito.verify(view).showError();
//    }

//    @Test
//    public void shouldDisplayErrorAsSettingUpProgressBar() throws Exception {
//        Mockito.when(userRepository.getUsers(meeting.MeetingID)).thenReturn(Single.<List<UserInfo>>error(new Throwable("error")));
//        presenter.loadMembers(meeting);
//        Mockito.verify(view).showError();
//    }



}