package com.in6206.collabdesk.config;

import com.in6206.collabdesk.entity.*;
import com.in6206.collabdesk.repository.BrandProfileRepository;
import com.in6206.collabdesk.repository.CreatorProfileRepository;
import com.in6206.collabdesk.repository.DealRepository;
import com.in6206.collabdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

/**
 * Demo data so every page has something to show. Only runs on an empty database,
 * so it will not overwrite data in MySQL.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final CreatorProfileRepository creators;
    private final BrandProfileRepository brands;
    private final DealRepository deals;
    private final PasswordEncoder encoder;
    private final Clock clock;

    @Override
    @Transactional
    public void run(String... args) {
        if (users.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now(clock);

        users.save(new User("admin", encoder.encode("admin123"), "Platform Admin", "admin@collabdesk.test", Role.ADMIN));

        CreatorProfile amy = creator("creator_amy", "Amy Tan", Platform.TIKTOK, "@amyeats.sg", 48_000,
                "Food & cafe", "450", "Weekly cafe hopping and hawker food reviews in Singapore.");
        CreatorProfile ken = creator("creator_ken", "Ken Lim", Platform.YOUTUBE, "KenTechTalk", 126_000,
                "Tech reviews", "1200", "Honest gadget reviews for students on a budget.");
        creator("creator_mei", "Mei Chen", Platform.XIAOHONGSHU, "梅子的留学日记", 9_500,
                "Student life", "200", "Study-abroad vlogs, dorm life and NTU campus tips.");

        BrandProfile bubbletea = brand("brand_bubbletea", "Lily (Marketing)", "Bobo Bubble Tea", "F&B", "https://example.com/bobo");
        BrandProfile techgear = brand("brand_techgear", "Raj (Partnerships)", "TechGear Asia", "Consumer electronics", "https://example.com/techgear");

        User b1 = bubbletea.getUser();
        User b2 = techgear.getUser();

        // 1. fresh proposal waiting for Amy
        Deal d1 = deal(bubbletea, amy, "New brown-sugar series launch", "One 30-60s TikTok tasting video at our Jurong outlet.",
                "400", today.plusDays(21));
        d1.addHistory(new DealStatusHistory(null, DealStatus.PROPOSED, b1, "Proposal sent"));

        // 2. Amy countered TechGear's offer, waiting for the brand
        Deal d2 = deal(techgear, amy, "Portable blender review", "Show the blender in a smoothie recipe video.",
                "300", today.plusDays(30));
        d2.setCounterAmount(new BigDecimal("480"));
        d2.setStatus(DealStatus.COUNTERED);
        d2.addHistory(new DealStatusHistory(null, DealStatus.PROPOSED, b2, "Proposal sent"));
        d2.addHistory(new DealStatusHistory(DealStatus.PROPOSED, DealStatus.COUNTERED, amy.getUser(),
                "Counter-offer 480 - recipe videos need extra filming time"));

        // 3. Ken accepted, script submitted after one revision, waiting for review
        Deal d3 = deal(bubbletea, ken, "Bubble tea x desk setup crossover", "Short segment in a desk setup video.",
                "900", today.plusDays(14));
        accepted(d3, b1, ken.getUser(), new BigDecimal("900"));
        Deliverable s3 = new Deliverable(DeliverableType.SCRIPT, today.plusDays(9));
        s3.setStatus(DeliverableStatus.SUBMITTED);
        s3.setRevisionCount(1);
        s3.setFeedback("Please mention the 1-for-1 promo code in the first 10 seconds.");
        s3.setContent("Intro: desk tour... (0:08) 'Use code KEN1FOR1 for 1-for-1 at Bobo!' ...");
        s3.setSubmittedAt(today.minusDays(1).atTime(20, 15));
        d3.addDeliverable(s3);

        // 4. Ken published 40 days ago on 30-day terms -> payment is late (run overdue check to flag it)
        Deal d4 = deal(techgear, ken, "Noise-cancelling earbuds review", "Dedicated 8-10 min YouTube review.",
                "1500", today.minusDays(42));
        accepted(d4, b2, ken.getUser(), new BigDecimal("1500"));
        Deliverable s4 = new Deliverable(DeliverableType.SCRIPT, today.minusDays(47));
        s4.setStatus(DeliverableStatus.APPROVED);
        s4.setContent("Full review script v2");
        d4.addDeliverable(s4);
        d4.setStatus(DealStatus.PUBLISHED);
        d4.setPublishedUrl("https://youtube.com/watch?v=demo-earbuds");
        d4.setPublishedDate(today.minusDays(40));
        d4.setPaymentDueDate(today.minusDays(10));
        d4.addHistory(new DealStatusHistory(DealStatus.ACCEPTED, DealStatus.PUBLISHED, ken.getUser(),
                "Published: https://youtube.com/watch?v=demo-earbuds"));

        // 5. a finished, paid deal for Amy's earnings
        Deal d5 = deal(bubbletea, amy, "Mid-autumn mooncake tea", "Festive TikTok, 1 video.", "380", today.minusDays(25));
        d5.setAgreedAmount(new BigDecimal("380"));
        d5.setStatus(DealStatus.PAID);
        d5.setPublishedUrl("https://tiktok.com/@amyeats.sg/video/demo");
        d5.setPublishedDate(today.minusDays(26));
        d5.setPaymentDueDate(today.plusDays(4));
        d5.addHistory(new DealStatusHistory(null, DealStatus.PROPOSED, b1, "Proposal sent"));
        d5.addHistory(new DealStatusHistory(DealStatus.PUBLISHED, DealStatus.PAID, b1, "Payment confirmed: 380"));

        // 6. Amy accepted but the script deadline passed yesterday -> becomes OVERDUE on next check
        Deal d6 = deal(techgear, amy, "Air fryer recipe series", "2 recipes using our air fryer.", "600", today.plusDays(4));
        accepted(d6, b2, amy.getUser(), new BigDecimal("600"));
        d6.addDeliverable(new Deliverable(DeliverableType.SCRIPT, today.minusDays(1)));

        deals.save(d1);
        deals.save(d2);
        deals.save(d3);
        deals.save(d4);
        deals.save(d5);
        deals.save(d6);
        log.info("Demo data loaded: 6 users, 6 deals. Password for all demo accounts: pass123 (admin: admin123)");
    }

    private CreatorProfile creator(String username, String name, Platform platform, String handle, int followers,
                                   String niche, String rate, String bio) {
        User u = users.save(new User(username, encoder.encode("pass123"), name, username + "@collabdesk.test", Role.CREATOR));
        CreatorProfile c = new CreatorProfile();
        c.setUser(u);
        c.setPlatform(platform);
        c.setHandle(handle);
        c.setFollowerCount(followers);
        c.setNiche(niche);
        c.setBaseRate(new BigDecimal(rate));
        c.setBio(bio);
        return creators.save(c);
    }

    private BrandProfile brand(String username, String name, String company, String industry, String website) {
        User u = users.save(new User(username, encoder.encode("pass123"), name, username + "@collabdesk.test", Role.BRAND));
        BrandProfile b = new BrandProfile();
        b.setUser(u);
        b.setCompanyName(company);
        b.setIndustry(industry);
        b.setWebsite(website);
        return brands.save(b);
    }

    private Deal deal(BrandProfile brand, CreatorProfile creator, String title, String brief, String budget, LocalDate due) {
        Deal d = new Deal();
        d.setBrand(brand);
        d.setCreator(creator);
        d.setTitle(title);
        d.setBrief(brief);
        d.setBudget(new BigDecimal(budget));
        d.setDueDate(due);
        return d;
    }

    private void accepted(Deal d, User brandUser, User creatorUser, BigDecimal amount) {
        d.setAgreedAmount(amount);
        d.setStatus(DealStatus.ACCEPTED);
        d.addHistory(new DealStatusHistory(null, DealStatus.PROPOSED, brandUser, "Proposal sent"));
        d.addHistory(new DealStatusHistory(DealStatus.PROPOSED, DealStatus.ACCEPTED, creatorUser, "Agreed at " + amount));
    }
}
