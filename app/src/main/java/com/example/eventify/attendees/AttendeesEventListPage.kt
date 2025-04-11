package com.example.eventify.attendees

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.R
import com.example.eventify.attendeesAdapter.AttendeesEventAdapter
import java.util.UUID

class AttendeesEventListPage : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var events: ArrayList<EventModelData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendees_event_list_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        events = ArrayList()
        //this is dummy data it will deleted after having database
        events.add(EventModelData(null, UUID.randomUUID().toString() , "Annual Beach Cleanup and Marine Protection Drive",
            "Every year, thousands of tons of plastic waste and pollutants wash up on our beaches, endangering marine life and disrupting coastal ecosystems. Our Annual Beach Cleanup and Marine Protection Drive is a large-scale event where volunteers of all ages come together to clean the shoreline, removing plastic debris, discarded fishing nets, and other pollutants. Through this hands-on initiative, we aim to restore the natural beauty of the coastline while raising awareness about the devastating effects of ocean pollution. Guided by marine conservationists, participants will learn about the impact of plastic on sea turtles, fish, and seabirds, and how small lifestyle changes can significantly reduce waste. \n\n"
                    + "In addition to the cleanup, this event will feature interactive workshops, environmental education booths, and guest talks from marine biologists who will share insights into ocean preservation. Attendees will have the opportunity to engage in fun and educational activities, including waste segregation challenges, sustainable living demonstrations, and upcycling craft sessions. Whether you are an environmental enthusiast or a first-time volunteer, this event is a great opportunity to contribute to a cleaner ocean while connecting with like-minded individuals passionate about protecting marine ecosystems. Together, we can take action and make a lasting impact on our planet.",
            "2025-04-05 08:00", "damansara HELP University","org1"))

        events.add(EventModelData(null, UUID.randomUUID().toString(), "Comprehensive Turtle Nesting Awareness and Conservation Workshop",
            "Sea turtles are among the oldest creatures on Earth, yet their survival is increasingly threatened by human activities such as coastal development, artificial lighting, and plastic pollution. This workshop is dedicated to raising awareness about the nesting habits of sea turtles and how we can work together to ensure their survival. Attendees will gain deep insights into the lifecycle of turtles, from egg-laying to hatching, and learn about the dangers that disrupt their reproductive success. Experts will provide hands-on demonstrations on monitoring nesting sites, protecting hatchlings from predators, and relocating nests in emergency situations. Participants will also get to witness the hatching process firsthand, a truly magical experience that underscores the importance of conservation efforts. \n\n"
                    + "Beyond education, this workshop empowers individuals with practical solutions to support turtle conservation. Participants will be introduced to eco-friendly beach management practices, sustainable tourism guidelines, and advocacy strategies for protecting endangered species. Additionally, volunteers will have the chance to take part in a beach patrol activity, helping to identify and document nesting sites for research purposes. This event is perfect for students, nature lovers, and conservationists eager to make a meaningful impact on marine wildlife. Join us and be a part of the movement to safeguard sea turtles for future generations!",
            "2025-04-10 10:30", "damansara HELP University","org2"))

        events.add(EventModelData(null, UUID.randomUUID().toString(), "Fundraising Gala and Charity Dinner for Global Turtle Conservation Efforts",
            "Our exclusive Fundraising Gala is an elegant evening dedicated to supporting global sea turtle conservation programs. This black-tie event will take place in a luxurious venue, featuring a gourmet dinner, live musical performances, and keynote speeches from renowned environmentalists. Guests will have the unique opportunity to network with marine biologists, conservationists, and philanthropists who share a common passion for protecting endangered sea turtle species. The night will also include a high-profile auction, where attendees can bid on rare artwork, sustainable luxury items, and eco-tourism experiences, with all proceeds going toward vital marine conservation projects worldwide. \n\n"
                    + "More than just a fundraiser, this gala aims to inspire action by showcasing success stories from past conservation efforts and highlighting urgent challenges that need immediate attention. Through captivating storytelling and visual presentations, guests will learn how donations contribute to the rehabilitation of injured turtles, habitat restoration, and anti-poaching initiatives. This glamorous yet impactful event serves as a reminder that everyone—regardless of background—has a role to play in preserving marine biodiversity. Join us for a night of elegance, generosity, and unwavering commitment to the future of our oceans.",
            "2025-04-15 19:00", "damansara HELP University","org3"))

        events.add(EventModelData(null, UUID.randomUUID().toString(), "International Webinar on Advanced Turtle Conservation Strategies",
            "With sea turtle populations declining at an alarming rate, conservationists are turning to advanced technologies and innovative strategies to combat threats such as climate change, habitat loss, and illegal poaching. This global webinar brings together leading researchers, scientists, and environmental policymakers to discuss cutting-edge approaches to turtle conservation. Attendees will learn about satellite tracking programs that monitor migration patterns, AI-driven research on nesting behaviors, and new legislative efforts aimed at protecting critical turtle habitats. The session will also delve into the role of local communities in conservation efforts and explore how sustainable fishing practices can reduce bycatch, a major threat to sea turtle survival. \n\n"
                    + "Designed to be interactive, this webinar will allow participants to engage in live Q&A sessions with experts, share their own conservation experiences, and collaborate on actionable solutions. Whether you are a student, activist, scientist, or simply someone who cares about marine life, this event offers a unique opportunity to deepen your understanding of sea turtle protection. By connecting individuals from across the globe, this webinar aims to foster a collective effort toward securing a sustainable future for these ancient and magnificent creatures. Don't miss out on this chance to be part of a crucial conversation on marine conservation.",
            "2025-04-20 14:00", "damansara HELP University","org4"))

        events.add(EventModelData(null, UUID.randomUUID().toString(), "Marine Life Protection Campaign and Educational Workshop",
            "The Marine Life Protection Campaign is a series of community-driven workshops designed to educate the public about the importance of preserving marine ecosystems. Experts will lead engaging sessions on coral reef conservation, the impact of pollution on marine species, and sustainable fishing practices. Attendees will gain hands-on experience with conservation techniques such as mangrove planting, water quality testing, and marine species monitoring. This initiative aims to equip individuals with the knowledge and skills to take action in protecting the oceans. \n\n"
                    + "In addition to educational sessions, the campaign will feature guest talks from leading environmentalists, interactive exhibits, and film screenings showcasing marine conservation efforts worldwide. Attendees will have the opportunity to network with scientists, activists, and organizations dedicated to marine protection. Whether you are an aspiring marine biologist or a concerned citizen, this event will provide valuable insights into how we can all contribute to preserving our oceans for future generations.",
            "2025-04-25 09:00", "damansara HELP University","org5"))

        events.add(EventModelData(null, UUID.randomUUID().toString(), "Underwater Cleanup Dive: Preserving Ocean Habitats",
            "Join our special underwater cleanup dive where certified divers will help remove harmful debris from the ocean floor. Every year, thousands of marine animals, including sea turtles, suffer due to plastic waste, lost fishing gear, and other pollutants contaminating their natural habitat. During this event, experienced divers will be equipped with eco-friendly gear to safely extract debris while ensuring minimal disruption to marine life. Non-divers can also contribute by assisting with surface-level waste collection and participating in educational sessions about responsible ocean stewardship. \n\n"
                    + "This initiative isn't just about cleaning the ocean—it’s about raising awareness of human impact on marine environments and inspiring long-term sustainable practices. Throughout the day, marine biologists will conduct live demonstrations on how pollution affects biodiversity and will share insights on how each individual can contribute to marine conservation. Join us for this meaningful event where every effort counts in making our oceans cleaner and safer for the creatures that call them home.",
            "2025-05-01 07:30", "damansara HELP University","org6"))

        events.add(EventModelData(null, UUID.randomUUID().toString(), "Turtle Rehabilitation Program: Healing and Conservation Efforts",
            "This hands-on event is dedicated to the rehabilitation and rescue of injured sea turtles. Participants will work alongside marine veterinarians and conservationists to understand the process of treating injured turtles, from initial rescue to recovery and eventual release back into the wild. You'll learn about common threats to turtle populations, such as boat strikes, plastic ingestion, and entanglement in fishing gear, and what can be done to prevent these dangers. The event will also feature live demonstrations of medical procedures performed on rescued turtles. \n\n"
                    + "Beyond medical care, attendees will engage in habitat restoration activities such as beach cleaning and nest monitoring, helping to create a safer environment for turtle populations. Interactive discussions will cover ways to support conservation efforts at home, including reducing plastic use and supporting ethical tourism practices. Whether you’re an aspiring marine biologist or a passionate environmentalist, this program provides an eye-opening experience that will deepen your understanding of marine wildlife conservation.",
            "2025-05-05 13:00", "damansara HELP University","org7"))

        events.add(EventModelData(null, UUID.randomUUID().toString(), "Eco-Friendly Living Workshop: Sustainable Habits for a Healthier Planet",
            "Living sustainably is crucial in protecting marine ecosystems, and this workshop will provide practical insights on reducing our environmental footprint. Experts in sustainability will guide attendees through eco-friendly lifestyle changes, including reducing single-use plastics, composting organic waste, and making ethical purchasing decisions. The workshop will also introduce participants to DIY sustainable products, such as natural cleaning solutions and reusable alternatives to everyday plastic items. \n\n"
                    + "Attendees will also learn about the direct impact of climate change on marine biodiversity and explore ways to advocate for stronger environmental policies. Through interactive discussions and hands-on activities, participants will leave with a better understanding of how simple daily choices can collectively lead to significant positive change. This workshop is ideal for anyone looking to adopt a greener lifestyle and contribute to global conservation efforts.",
            "2025-05-10 11:00", "damansara HELP University","org8"))

        events.add(EventModelData(null, UUID.randomUUID().toString(), "Turtle Migration Awareness Walk: Protecting the Paths of Endangered Species",
            "Turtle migration is one of nature’s most fascinating journeys, but human interference has made these ancient routes increasingly difficult to navigate. This awareness walk will take participants along coastal areas known for turtle nesting and migration, highlighting the importance of preserving these natural pathways. Experts will share insights into how artificial lighting, pollution, and beach development disrupt turtle migration and nesting behaviors. Along the way, attendees will learn how they can contribute to conservation efforts, such as supporting protected beach zones and advocating for responsible tourism. \n\n"
                    + "The walk will also serve as a community engagement initiative, encouraging individuals to take action by spreading awareness about conservation challenges. Participants will have the opportunity to sign petitions, join conservation groups, and pledge to adopt sustainable beach practices. Whether you’re a seasoned environmentalist or just starting your journey into conservation, this event is an eye-opening experience that fosters deeper appreciation for marine life and the ecosystems they rely on.",
            "2025-05-15 16:30", "damansara HELP University","org9"))

        events.add(EventModelData(null, UUID.randomUUID().toString(), "Sea Turtle Festival: Celebrating Conservation Through Culture and Community",
            "The Sea Turtle Festival is an exciting community event that combines conservation awareness with cultural celebrations. This family-friendly festival will feature live performances, interactive exhibits, and educational workshops focused on the importance of turtle conservation. Visitors will have the chance to meet marine biologists, participate in beach cleanups, and engage in hands-on activities such as turtle tracking and nest protection simulations. Food stalls offering sustainable seafood and plant-based alternatives will also be available, emphasizing the importance of responsible consumption choices in preserving marine life. \n\n"
                    + "Beyond education, the festival aims to create a fun and inspiring atmosphere that encourages people of all ages to take an active role in conservation. There will be art installations made from recycled ocean plastic, storytelling sessions about indigenous coastal traditions, and live music performances celebrating nature and sustainability. By blending environmental advocacy with entertainment, the festival seeks to inspire long-term commitment to marine protection. Whether you're an ocean enthusiast or simply looking for an enjoyable day out, the Sea Turtle Festival offers something for everyone while supporting an important cause.",
            "2025-05-20 18:00", "damansara HELP University","org10"))


        recyclerView = findViewById(R.id.attendeesEventListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = AttendeesEventAdapter(events)
        recyclerView.adapter = adapter

        adapter.setOnClickEventListener(object: AttendeesEventAdapter.onClickEventListener{
            override fun onClickItem(data: EventModelData) {
                onClick(data)
            }
        })
    }

    fun onClick(event: EventModelData){
        val intent = Intent(this,AttendeesEventDetail::class.java)
        intent.putExtra(AttendeesEventDetail.EXTRA_EVENT_DETAIL,event)
        startActivity(intent)

    }
}