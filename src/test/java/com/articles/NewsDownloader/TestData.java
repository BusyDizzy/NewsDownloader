package com.articles.NewsDownloader;

import com.articles.NewsDownloader.entity.Article;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TestData {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static final MatcherFactory.Matcher<Article> ARTICLE_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Article.class, "articleContent", "id");

    public static final Article article1 = new Article(1L, "SpaceX completes 28th commercial resupply mission to ISS", "Teslarati",
            "https://www.teslarati.com/spacex-completes-28th-commercial-resupply-mission-to-iss/",
            LocalDateTime.parse("2023-07-06T16:14:48", DATE_TIME_FORMATTER), null);
    public static final Article article2 = new Article(2L, "Ariane V goes out in style, retires after 27 years of service", "Teslarati",
            "https://www.teslarati.com/ariane-v-retires-after-27-years-of-service/",
            LocalDateTime.parse("2023-07-06T16:24:39", DATE_TIME_FORMATTER), null);
    public static final Article article3 = new Article(3L, "SpaceX focuses on launch site readiness ahead of Starship Flight 2",
            "NASASpaceflight", "https://www.nasaspaceflight.com/2023/07/starship-launch-site-readiness/",
            LocalDateTime.parse("2023-07-06T17:29:43", DATE_TIME_FORMATTER), null);
    public static final Article article4 = new Article(4L, "Students Reach New Heights With Spaceport Nova Scotia First Launch ",
            "The Launch Pad",
            "https://tlpnetwork.com/news/2023/07/students-reach-new-heights-with-spaceport-nova-scotia-first-launch",
            LocalDateTime.parse("2023-07-06T22:31:00", DATE_TIME_FORMATTER), null);
    public static final Article article5 = new Article(5L, "California Science Center GO For Shuttle Endeavour Stacking", "The Launch Pad",
            "https://tlpnetwork.com/news/2023/07/california-science-center-go-for-shuttle-endeavour-stacking",
            LocalDateTime.parse("2023-07-06T18:34:00", DATE_TIME_FORMATTER), null);
    public static final Article article6 = new Article(6L,
            "ESA Launch Independent Enquiry Commission to Investigate Vega C Z40 Test Failure", "European Spaceflight",
            "https://europeanspaceflight.com/esa-launch-independent-enquiry-commission-to-investigate-vega-c-z40-test-failure/",
            LocalDateTime.parse("2023-07-06T10:50:12", DATE_TIME_FORMATTER), null);
    public static final Article article7 = new Article(7L,
            "Space Command argues for shift from static to dynamic satellite operations", "SpaceNews",
            "https://spacenews.com/space-command-argues-for-shift-from-static-to-dynamic-satellite-operations/",
            LocalDateTime.parse("2023-07-06T20:00:34", DATE_TIME_FORMATTER), null);
    public static final Article article8 = new Article(8L, "China’s Landspace set for second methalox rocket launch", "SpaceNews",
            "https://spacenews.com/chinas-landspace-set-for-second-methalox-rocket-launch/",
            LocalDateTime.parse("2023-07-06T20:14:40", DATE_TIME_FORMATTER), null);
    public static final Article article9 = new Article(9L,
            "Viasat signs deal to commercialize European airspace tracking service", "SpaceNews",
            "https://spacenews.com/viasat-signs-deal-to-commercialize-european-airspace-tracking-service/",
            LocalDateTime.parse("2023-07-06T20:59:40", DATE_TIME_FORMATTER), null);
    public static final Article article10 = new Article(10L, "Europe leans on SpaceX to bridge launcher gap", "SpaceNews",
            "https://spacenews.com/europe-leans-on-spacex-to-bridge-launcher-gap/",
            LocalDateTime.parse("2023-07-06T22:29:28", DATE_TIME_FORMATTER), null);
    public static final Article article11 = new Article(11L, "Final Ariane 5 Takes Flight", "SpacePolicyOnline.com",
            "https://spacepolicyonline.com/news/final-ariane-5-takes-flight/",
            LocalDateTime.parse("2023-07-06T02:29:08", DATE_TIME_FORMATTER), null);
    public static final Article article12 = new Article(12L, "Rivada gets more breathing room to deploy constellation", "SpaceNews",
            "https://spacenews.com/rivada-gets-more-breathing-room-to-deploy-constellation/",
            LocalDateTime.parse("2023-07-05T15:11:35", DATE_TIME_FORMATTER), null);
    public static final Article article13 = new Article(13L, "Ariane 5 launches for the final time", "SpaceNews",
            "https://spacenews.com/ariane-5-launches-for-the-final-time/",
            LocalDateTime.parse("2023-07-05T23:24:16", DATE_TIME_FORMATTER), null);
    public static final Article article14 = new Article(14L,
            "HawkEye 360 satellites to monitor illegal fishing in Pacific Islands", "SpaceNews",
            "https://spacenews.com/hawkeye-360-satellites-to-monitor-illegal-fishing-in-pacific-islands/",
            LocalDateTime.parse("2023-07-06T08:00:00", DATE_TIME_FORMATTER), null);
    public static final Article article15 = new Article(15L,
            "Regulatory uncertainty as commercial human spaceflight takes off", "SpaceNews",
            "https://spacenews.com/regulatory-uncertainty-as-commercial-human-spaceflight-takes-off/",
            LocalDateTime.parse("2023-07-05T09:00:00", DATE_TIME_FORMATTER), null);
    public static final Article article16 = new Article(16L,
            "Radio noise from satellite constellations could interfere with astronomers", "SpaceNews",
            "https://spacenews.com/radio-noise-from-satellite-constellations-could-interfere-with-astronomers/",
            LocalDateTime.parse("2023-07-05T11:45:17", DATE_TIME_FORMATTER), null);
    public static final Article article17 = new Article(17L, "Goodbye to the Ariane 5, the ‘Swiss Knife’ of Europe’s launch industry",
            "NASASpaceflight", "https://www.nasaspaceflight.com/2023/07/goodbye-ariane-5/",
            LocalDateTime.parse("2023-07-05T15:41:14", DATE_TIME_FORMATTER), null);
    public static final Article article18 = new Article(18L, "The European Space Forum Returns to Brussels", "The Launch Pad",
            "https://tlpnetwork.com/news/2023/07/european_space_forum_returns_to_brussels",
            LocalDateTime.parse("2023-07-04T22:15:00", DATE_TIME_FORMATTER), null);
    public static final Article article19 = new Article(19L, "Poland Complete Acceptance Phase for Three Space Debris Observatories",
            "European Spaceflight",
            "https://europeanspaceflight.com/poland-complete-acceptance-phase-for-three-space-debris-observatories/",
            LocalDateTime.parse("2023-07-05T14:16:19", DATE_TIME_FORMATTER), null);
    public static final Article article20 = new Article(20L, "Europe’s Ariane 5 rocket to make its final launch this evening [Updated]",
            "Arstechnica",
            "https://arstechnica.com/space/2023/07/europes-venerable-ariane-5-rocket-faces-a-bittersweet-ending-on-tuesday/",
            LocalDateTime.parse("2023-07-05T13:50:55", DATE_TIME_FORMATTER), null);

    public static final List<Article> articlesList = List.of(article1, article2, article3, article4,
            article5, article6, article7, article8, article9, article10, article11, article12,
            article13, article14, article15, article16, article17, article18, article19, article20
    );
}

