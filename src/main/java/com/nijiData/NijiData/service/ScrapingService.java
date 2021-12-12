package com.nijiData.NijiData.service;

import com.nijiData.NijiData.entity.Member;
import com.nijiData.NijiData.repository.ScrapingRepository;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ScrapingService {

    @Autowired
    ScrapingRepository scrapingRepository;

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final com.google.api.client.json.JsonFactory JSON_FACTORY = new JacksonFactory();
    //APIキー
    String key = "AIzaSyCfLSCfatZoJJ7asA404PkrZG5lf4Servc";
    //検索実行
    ChannelListResponse channelsResponse;

    //サイトからメンバーの名前とIDを取得するメソッド
    public List<Member> getNameAndId() throws IOException {
        Document document = Jsoup.connect("https://refined-itsukara-link.neet.love/livers").get();
        List<Member> memberList = new ArrayList<>();

        //チャンネルID取得
        Elements hrefs = document.select("a[href*=YouTube]");
        int idNum = 1;
        for (Element element: hrefs){
            Member member = new Member();
            String href = element.attr("href");
            member.setChannelId(href.substring(32));
            member.setId(idNum);
            memberList.add(member);
            idNum++;
        }

        //名前取得
        int num = 0;
        Elements names = document.select("div.flex-grow > p.ease-out");
        for (Element element: names){
            Member member = memberList.get(num);
            String name = element.text();
            member.setName(name);
            num++;
        }
        System.out.println(memberList.size() + "人のIDと名前を取得完了");
        return memberList;
    }

    //channelIDを用いてyoutubeから各メンバーのチャンネル情報を取得。
    public List<Channel> getMemberInfoFromYoutube(List<Member> nameAndIdList) throws IOException{
        YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("youtube-cmdline-search-sample").build();

        List<Channel> channelsList = new ArrayList<Channel>();

        for (Member member : nameAndIdList) {
            String chId = member.getChannelId();
            YouTube.Channels.List channelInfo = youtube.channels().list("id,snippet,statistics");
            channelInfo.setKey(key);
            channelInfo.setId(chId);

            channelsResponse = channelInfo.execute();
            Channel channel = channelsResponse.getItems().get(0);
            channelsList.add(channel);
        }
        return channelsList;
    }

    public void saveMemberInfo(List<Member> nameAndIdList, List<Channel> channelsList){
        //DBのチャンネルIDとyoutubeから取得したチャンネルIDが合っていた場合登録
        List<String> chIdList = scrapingRepository.findChid();
        int idCnt = 1;
        for(Channel channel:channelsList) {
            for(Member nameAndId:nameAndIdList){
                if(channel.getId().equals(nameAndId.getChannelId())){
                    System.out.println("一致しました！");
                    Member member = new Member();

                    //チャンネル開設日
                    Date publishedAt_ = null;
                    DateTime publishedAt =  channel.getSnippet().getPublishedAt();
                    String datetimeStr = publishedAt.toString();
                    int n = datetimeStr.indexOf("T");
                    String dateStr = datetimeStr.substring(0, n);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try{
                        publishedAt_ = format.parse(dateStr);
                    }catch (ParseException e) {
                        e.printStackTrace();
                    }
                    member.setPublishedAt(publishedAt_);

                    member.setId(idCnt);
                    member.setName(nameAndId.getName());
                    member.setChannelId(nameAndId.getChannelId());
                    member.setThumbnail(channel.getSnippet().getThumbnails().getHigh().getUrl());
                    member.setVideoCount(String.format("%,d", channel.getStatistics().getVideoCount().intValue()));
                    member.setSubscriber(String.format("%,d", channel.getStatistics().getSubscriberCount().intValue()));
                    scrapingRepository.save(member);
                    break;
                }
            }
            idCnt++;
        }
    }
}
