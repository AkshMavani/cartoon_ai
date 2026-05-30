package com.skylock.ai_cartoon.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.skylock.ai_cartoon.R
import com.skylock.ai_cartoon.adapter.HomeToolAdapter
import com.skylock.ai_cartoon.fragment.CartoonStyleFragment
import com.skylock.ai_cartoon.model.CartoonStyle
import com.skylock.ai_cartoon.util.ToolEnhanceUtils

class MainActivity : AppCompatActivity() {
    private var recycleTool: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupTabLayout()
        // Initialize Firebase Analytics instance
        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        // Bind and initialize the tool Recycler View layout component
        recycleTool = findViewById<RecyclerView?>(R.id.recycleTool)
        recycleTool?.setLayoutManager(layoutManager)
        initActivityTool()
    }

    private fun setupTabLayout() {
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        val tabs = listOf(
            "All" to getAllStyles(),
            "Cartoon" to getCartoonStyles(),
            "Hair" to getHairStyles(),
            "Business " to getBusinessWomanStyles(),
            "Movie Woman" to getMovieWomanStyles(),
            "Driver Woman" to getDriverWomanStyles(),
            "Media Woman" to getMediaWomanStyles(),
            "Birthday Man" to getBirthdayManStyles(),
            "Valentine Woman" to getValentineWomanStyles(),
            "Winter Woman" to getWinterWomanStyles(),
            "Newyear Woman" to getNewyearWomanStyles(),
            "Cute Woman" to getCuteWomanStyles(),
            "Reward Woman" to getRewardWomanStyles(),
            "Edgy Woman" to getEdgyWomanStyles(),
            "Modern Woman" to getModernWomanStyles(),
            "Cinematic Woman" to getCinematicWomanStyles(),
            "Spotlight Woman" to getSpotlightWomanStyles(),
            "Golden Woman" to getGoldenWomanStyles(),
            "Circle Woman" to getCircleWomanStyles(),
            "Cube Woman" to getCubeWomanStyles(),
            "Studio Woman" to getStudioWomanStyles(),
            "Bwstudio Woman" to getBwstudioWomanStyles(),
            "Monochromatic Woman" to getMonochromaticWomanStyles(),
            "LinkedIn Woman" to getLinkedInWomanStyles(),
            "Suit Woman" to getSuitWomanStyles(),
            "Christmas Woman" to getChristmasWomanStyles(),
            "BabyChristmas Woman" to getBabyChristmasWomanStyles(),
            "Lunar Woman" to getLunarWomanStyles(),
            "VNLuna Woman" to getVNLunaWomanStyles(),
            "Aging Woman" to getAgingWomanStyles(),
            "Haircut" to getHaircutStyles(),    // <-- ADD
            "Trendings" to getFigurineStyles(),  // <-- ADD
            "AI Photo Cartoon" to getAIPhotoCartoonStyles(),
            "3D Cartoon" to get3DCartoonStyles(),
            "Comic" to getComicStyles(),
            "Toon Mix" to getToonmixStyles(),
            "Illustration" to getIllustrationStyles()
        )

        viewPager.adapter = StylePagerAdapter(this, tabs.map { it.second })

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position].first
        }.attach()
    }

    inner class StylePagerAdapter(
        activity: FragmentActivity,
        private val stylesList: List<List<CartoonStyle>>
    ) : FragmentStateAdapter(activity) {

        override fun getItemCount() = stylesList.size

        override fun createFragment(position: Int): Fragment {
            return CartoonStyleFragment.newInstance(stylesList[position])
        }
    }

    fun goAIAvatar(style: String, feature: String, isGender: Boolean) {
        val intent = Intent(this, CartoonIntroActivity::class.java).apply {
            putExtra("style", style)
            putExtra("feature", feature)
            putExtra("is_gender", isGender)
        }
        startActivity(intent)
    }

    // ── Data ──────────────────────────────────────────────────────────────

    private fun getAllStyles(): List<CartoonStyle> =
        getCartoonStyles() + getHairStyles() + getBusinessWomanStyles() + getMovieWomanStyles() + getDriverWomanStyles() + getMediaWomanStyles() +
                getBirthdayManStyles() +
                getValentineWomanStyles() +
                getWinterWomanStyles() +
                getNewyearWomanStyles() +
                getCuteWomanStyles() +
                getRewardWomanStyles() +
                getEdgyWomanStyles() +
                getModernWomanStyles() +
                getCinematicWomanStyles() +
                getSpotlightWomanStyles() +
                getGoldenWomanStyles() +
                getCircleWomanStyles() +
                getCubeWomanStyles() +
                getStudioWomanStyles() +
                getBwstudioWomanStyles() +
                getMonochromaticWomanStyles() +
                getLinkedInWomanStyles() +
                getSuitWomanStyles() +
                getChristmasWomanStyles() +
                getBabyChristmasWomanStyles() +
                getLunarWomanStyles() +
                getVNLunaWomanStyles() +
                getAgingWomanStyles() +
                getHaircutStyles() + getFigurineStyles()

    // ── Cartoon & Hair (unchanged) ─────────────────────────────────────────

    private fun getCartoonStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Ghibli",
            iconUrl = "https://iili.io/C3FlCkg.png",
            styleKey = "ghibli_runninghub_1946470668230619137"
        ),
        CartoonStyle(
            name = "3D Emoji",
            iconUrl = "https://iili.io/C3FlvZx.png",
            styleKey = "3demoji"
        ),
        CartoonStyle(
            name = "GPT-4o Travel",
            iconUrl = "https://iili.io/C3FlyFI.jpg",
            styleKey = "polaroid"
        ),
        //below  left
        CartoonStyle(
            name = "GPT-4o Best Friend",
            iconUrl = "https://iili.io/C3FU3OJ.jpg",
            styleKey = "polaroid2"
        ),
        CartoonStyle(
            name = "GPT-4o Summer",
            iconUrl = "https://iili.io/C3F0EHF.jpg",
            styleKey = "polaroid3summer"
        ),
        CartoonStyle(
            name = "Qwen 3D Chibi",
            iconUrl = "https://iili.io/C3F0bDb.png",
            styleKey = "qwen3dchibi"
        ),
        CartoonStyle(
            name = "Pixar 3D",
            iconUrl = "https://iili.io/C3F1azG.png",
            styleKey = "qwenpixar3d"
        ),
        CartoonStyle(
            name = "Avatar",
            iconUrl = "https://iili.io/C3F6d1p.jpg",
            styleKey = "avatar"
        ),
        CartoonStyle(
            name = "Wool",
            iconUrl = "https://iili.io/C3F6t6l.png",
            styleKey = "wool"
        ),
        CartoonStyle(
            name = "Keychain",
            iconUrl = "https://iili.io/C3FPwSS.jpg",
            styleKey = "keychain"
        ),
        CartoonStyle(
            name = "Comic",
            iconUrl = "https://iili.io/C3FsPu1.png",
            styleKey = "qwencomic"
        ),
        CartoonStyle(
            name = "Anime",
            iconUrl = "https://iili.io/C3FQ3OP.md.png",
            styleKey = "qwenanime"
        ),
        CartoonStyle(
            name = "Clay",
            iconUrl = "https://iili.io/C3FZWaR.png",
            styleKey = "qwenclay"
        ),
        CartoonStyle(
            name = "Jojo",
            iconUrl = "https://iili.io/C3FbRaf.md.png",
            styleKey = "qwenjojo"
        ),
        CartoonStyle(
            name = "Lego",
            iconUrl = "https://iili.io/C3FmSxS.md.png",
            styleKey = "qwenlego"
        ),
        CartoonStyle(
            name = "Line Art",
            iconUrl = "https://iili.io/C3Fp3eR.png",
            styleKey = "qwenlineart"
        ),
        CartoonStyle(
            name = "Macaron",
            iconUrl = "https://iili.io/C3FprIp.png",
            styleKey = "qwenmacaron"
        ),
        CartoonStyle(
            name = "Oil Painting",
            iconUrl = "https://iili.io/C3K9WiP.md.png",
            styleKey = "qwenoilpainting"
        ),
        CartoonStyle(
            name = "Origami",
            iconUrl = "https://iili.io/C3KHT7a.png",
            styleKey = "qwenorigami"
        ),
        CartoonStyle(
            name = "Paper Cut",
            iconUrl = "https://iili.io/C3KduxR.png",
            styleKey = "qwenpaper"
        ),
        CartoonStyle(
            name = "Picasso",
            iconUrl = "https://iili.io/C3KdsdF.png",
            styleKey = "qwenpicasso"
        ),
        CartoonStyle(
            name = "Pixel Art",
            iconUrl = "https://iili.io/C3K2E21.png",
            styleKey = "qwenpixel"
        ),
        CartoonStyle(
            name = "Pop Art",
            iconUrl = "https://iili.io/C3K3znR.md.png",
            styleKey = "qwenpopart"
        ),
        CartoonStyle(
            name = "Van Gogh",
            iconUrl = "https://iili.io/C3K3vMQ.png",
            styleKey = "qwenvangogh"
        ),
        CartoonStyle(
            name = "Graffiti",
            iconUrl = "https://iili.io/C3KfaAF.md.png",
            styleKey = "qwengraffiti"
        ),
        CartoonStyle(
            name = "GTA 5 Style",
            iconUrl = "https://iili.io/C3KKv3B.png",
            styleKey = "qwengta5"
        ),
        CartoonStyle(
            name = "Simpsons Style",
            iconUrl = "https://iili.io/C3KB6Zu.png",
            styleKey = "qwensimpsons"
        )
    )

    private fun getHairStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Black Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_black_1779616975121.jpg",
            styleKey = "black"
        ),
        CartoonStyle(
            name = "Blonde Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_blonde_1779616829597.jpg",
            styleKey = "blonde"
        ),
        CartoonStyle(
            name = "Pink Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_pink_1779616812345.jpg",
            styleKey = "pink"
        ),
        CartoonStyle(
            name = "Purple Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_purple_1779616972551.jpg",
            styleKey = "purple"
        ),
        CartoonStyle(
            name = "Orange Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_orange_1779617300751.jpg",
            styleKey = "orange"
        ),
        CartoonStyle(
            name = "Blue Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_blue_1779617124198.jpg",
            styleKey = "blue"
        ),
        CartoonStyle(
            name = "Green Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_green_1779617328062.jpg",
            styleKey = "green"
        ),
        CartoonStyle(
            name = "Rainbow Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_rainbow_1779617543151.jpg",
            styleKey = "rainbow"
        ),
        CartoonStyle(
            name = "Red Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_red_1779617595286.jpg",
            styleKey = "red"
        ),
        CartoonStyle(
            name = "Silver Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_silver_1779617742259.jpg",
            styleKey = "silver"
        ),
        CartoonStyle(
            name = "Yellow Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_yellow_1779617698367.jpg",
            styleKey = "yellow"
        ),
        CartoonStyle(
            name = "Brown Hair",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_brown_1779617854613.jpg",
            styleKey = "brown"
        )
    )

    private fun getBusinessWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Business Blue",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Business/woman/Blue.webp",
            styleKey = "Business_Blue", isGender = true
        ),
        CartoonStyle(
            name = "Business Black",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Business/woman/Black.webp",
            styleKey = "Business_Black", isGender = true
        ),
        CartoonStyle(
            name = "Business Light",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Business/woman/Light.webp",
            styleKey = "Business_Light", isGender = true
        ),
        CartoonStyle(
            name = "Business Yellow",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Business/woman/Yellow.webp",
            styleKey = "Business_Yellow", isGender = true
        ),
        CartoonStyle(
            name = "Business Red",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Business/woman/Red.webp",
            styleKey = "Business_Red", isGender = true
        ),
        CartoonStyle(
            name = "Business Beige",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Business/woman/Beige.webp",
            styleKey = "Business_Beige", isGender = true
        ),
        CartoonStyle(
            name = "Business Navy",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Business/woman/Navy.webp",
            styleKey = "Business_Navy", isGender = true
        )
    )

    private fun getMovieWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Movie Blur",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Movie/woman/Blur.webp",
            styleKey = "Movie_Blur", isGender = true
        ),
        CartoonStyle(
            name = "Movie Street",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Movie/woman/Street.webp",
            styleKey = "Movie_Street", isGender = true
        ),
        CartoonStyle(
            name = "Movie Grassland",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Movie/woman/Grassland.webp",
            styleKey = "Movie_Grassland", isGender = true
        ),
        CartoonStyle(
            name = "Movie News",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Movie/woman/News.webp",
            styleKey = "Movie_News", isGender = true
        ),
        CartoonStyle(
            name = "Movie Sky",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Movie/woman/Sky.webp",
            styleKey = "Movie_Sky", isGender = true
        ),
        CartoonStyle(
            name = "Movie Pool",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Movie/woman/Pool.webp",
            styleKey = "Movie_Pool", isGender = true
        )
    )

    private fun getDriverWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Driver Cyan",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Driver/woman/Cyan.webp",
            styleKey = "Driver_Cyan",
            isGender = true
        ),
        CartoonStyle(
            name = "Driver Red",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Driver/woman/Red.webp",
            styleKey = "Driver_Red",
            isGender = true
        ),
        CartoonStyle(
            name = "Driver Yellow",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Driver/woman/Yellow.webp",
            styleKey = "Driver_Yellow",
            isGender = true
        ),
        CartoonStyle(
            name = "Driver Gray",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Driver/woman/Gray.webp",
            styleKey = "Driver_Gray",
            isGender = true
        ),
        CartoonStyle(
            name = "Driver Dark",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Driver/woman/Dark.webp",
            styleKey = "Driver_Dark",
            isGender = true
        ),
        CartoonStyle(
            name = "Driver Light",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Driver/woman/Light.webp",
            styleKey = "Driver_Light",
            isGender = true
        )
    )

    private fun getMediaWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Media Chair",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Media/woman/Chair.webp",
            styleKey = "Media_Chair",
            isGender = true
        ),
        CartoonStyle(
            name = "Media Light",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Media/woman/Light.webp",
            styleKey = "Media_Light",
            isGender = true
        ),
        CartoonStyle(
            name = "Media Black",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Media/woman/Black.webp",
            styleKey = "Media_Black",
            isGender = true
        ),
        CartoonStyle(
            name = "Media Gray",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Media/woman/Gray.webp",
            styleKey = "Media_Gray",
            isGender = true
        ),
        CartoonStyle(
            name = "Media Brown",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Media/woman/Brown.webp",
            styleKey = "Media_Brown",
            isGender = true
        ),
        CartoonStyle(
            name = "Media Dark",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Media/woman/Dark.webp",
            styleKey = "Media_Dark",
            isGender = true
        )
    )
    // ── Birthday ───────────────────────────────────────────────────────────

    private fun getBirthdayManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Birthday Lighting",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_woman_Birthday_Lighting_1779729599094.jpg",
            styleKey = "Birthday_Lighting", isGender = true
        ),
        CartoonStyle(
            name = "Birthday Black",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_man_Birthday_Black_1779730540107.jpg",
            styleKey = "Birthday_Black", isGender = true
        ),
        CartoonStyle(
            name = "Birthday Golden",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_woman_Birthday_Golden_1779730828043.jpg",
            styleKey = "Birthday_Golden", isGender = true
        ),
        CartoonStyle(
            name = "Birthday Purple",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/man/Purple.webp",
            styleKey = "Birthday_Purple", isGender = true
        ),
        CartoonStyle(
            name = "Birthday Red",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/man/Red.webp",
            styleKey = "Birthday_Red", isGender = true
        ),
        CartoonStyle(
            name = "Birthday Disco",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/man/Disco.webp",
            styleKey = "Birthday_Disco", isGender = true
        )
    )

    // ── Valentine ──────────────────────────────────────────────────────────

    /* private fun getValentineManStyles(): List<CartoonStyle> = listOf(
         CartoonStyle(
             name = "Valentine Nightcity",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Nightcity.webp",
             styleKey = "man_Valentine_Nightcity"
         ),
         CartoonStyle(
             name = "Valentine Oldmoney",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Oldmoney.webp",
             styleKey = "man_Valentine_Oldmoney"
         ),
         CartoonStyle(
             name = "Valentine Date",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Date.webp",
             styleKey = "man_Valentine_Date"
         ),
         CartoonStyle(
             name = "Valentine Party",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Party.webp",
             styleKey = "man_Valentine_Party"
         ),
         CartoonStyle(
             name = "Valentine Paris",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Paris.webp",
             styleKey = "man_Valentine_Paris"
         ),
         CartoonStyle(
             name = "Valentine Candle",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Candle.webp",
             styleKey = "man_Valentine_Candle"
         )
     )*/

    private fun getValentineWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Valentine Nightcity",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Nightcity.webp",
            styleKey = "Valentine_Nightcity",
            isGender = true
        ),
        CartoonStyle(
            name = "Valentine Oldmoney",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Oldmoney.webp",
            styleKey = "Valentine_Oldmoney",
            isGender = true
        ),
        CartoonStyle(
            name = "Valentine Date",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Date.webp",
            styleKey = "Valentine_Date",
            isGender = true
        ),
        CartoonStyle(
            name = "Valentine Party",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Party.webp",
            styleKey = "Valentine_Party",
            isGender = true
        ),
        CartoonStyle(
            name = "Valentine Paris",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Paris.webp",
            styleKey = "Valentine_Paris",
            isGender = true
        ),
        CartoonStyle(
            name = "Valentine Candle",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Candle.webp",
            styleKey = "Valentine_Candle",
            isGender = true
        )
    )

    // ── Winter ─────────────────────────────────────────────────────────────

    /*   private fun getWinterManStyles(): List<CartoonStyle> = listOf(
           CartoonStyle(
               name = "Winter Lighting",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/Lighting.webp",
               styleKey = "man_Winter_Lighting"
           ),
           CartoonStyle(
               name = "Winter Hooded",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/Hooded.webp",
               styleKey = "man_Winter_Hooded"
           ),
           CartoonStyle(
               name = "Winter Umbrella",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/Umbrella.webp",
               styleKey = "man_Winter_Umbrella"
           ),
           CartoonStyle(
               name = "Winter BlueSpruce",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/BlueSpruce.webp",
               styleKey = "man_Winter_BlueSpruce"
           ),
           CartoonStyle(
               name = "Winter Snowman",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/Snowman.webp",
               styleKey = "man_Winter_Snowman"
           ),
           CartoonStyle(
               name = "Winter Aurora",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/Aurora.webp",
               styleKey = "man_Winter_Aurora"
           )
       )*/

    private fun getWinterWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Winter Lighting",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/Lighting.webp",
            styleKey = "Winter_Lighting",
            isGender = true
        ),
        CartoonStyle(
            name = "Winter Hooded",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/Hooded.webp",
            styleKey = "Winter_Hooded",
            isGender = true
        ),
        CartoonStyle(
            name = "Winter Umbrella",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/Umbrella.webp",
            styleKey = "Winter_Umbrella",
            isGender = true
        ),
        CartoonStyle(
            name = "Winter BlueSpruce",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/BlueSpruce.webp",
            styleKey = "Winter_BlueSpruce",
            isGender = true
        ),
        CartoonStyle(
            name = "Winter Snowman",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/Snowman.webp",
            styleKey = "Winter_Snowman",
            isGender = true
        ),
        CartoonStyle(
            name = "Winter Aurora",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/Aurora.webp",
            styleKey = "Winter_Aurora",
            isGender = true
        )
    )

    // ── Newyear ────────────────────────────────────────────────────────────

    /*  private fun getNewyearManStyles(): List<CartoonStyle> = listOf(
          CartoonStyle(
              name = "Newyear Sofa",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/Sofa.webp",
              styleKey = "man_Newyear_Sofa"
          ),
          CartoonStyle(
              name = "Newyear Disco",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/Disco.webp",
              styleKey = "man_Newyear_Disco"
          ),
          CartoonStyle(
              name = "Newyear Golden",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/Golden.webp",
              styleKey = "man_Newyear_Golden"
          ),
          CartoonStyle(
              name = "Newyear Balloons",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/Balloons.webp",
              styleKey = "man_Newyear_Balloons"
          ),
          CartoonStyle(
              name = "Newyear Party",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/Party.webp",
              styleKey = "man_Newyear_Party"
          ),
          CartoonStyle(
              name = "Newyear Yacht",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/yacht.webp",
              styleKey = "man_Newyear_yacht"
          )
      )
  */
    private fun getNewyearWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Newyear Sofa",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/Sofa.webp",
            styleKey = "Newyear_Sofa",
            isGender = true
        ),
        CartoonStyle(
            name = "Newyear Disco",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/Disco.webp",
            styleKey = "Newyear_Disco",
            isGender = true
        ),
        CartoonStyle(
            name = "Newyear Golden",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/Golden.webp",
            styleKey = "Newyear_Golden",
            isGender = true
        ),
        CartoonStyle(
            name = "Newyear Balloons",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/Balloons.webp",
            styleKey = "Newyear_Balloons",
            isGender = true
        ),
        CartoonStyle(
            name = "Newyear Party",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/Party.webp",
            styleKey = "Newyear_Party",
            isGender = true
        ),
        CartoonStyle(
            name = "Newyear Yacht",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/yacht.webp",
            styleKey = "Newyear_yacht",
            isGender = true
        )
    )
    // ── Cute ───────────────────────────────────────────────────────────────

    /* private fun getCuteManStyles(): List<CartoonStyle> = listOf(
         CartoonStyle(
             name = "Cute Hood",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/man/Hood.webp",
             styleKey = "man_Cute_Hood"
         ),
         CartoonStyle(
             name = "Cute Cheek",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/man/Cheek.webp",
             styleKey = "man_Cute_Cheek"
         ),
         CartoonStyle(
             name = "Cute Doll",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/man/Doll.webp",
             styleKey = "man_Cute_Doll"
         ),
         CartoonStyle(
             name = "Cute Giant",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/man/Giant.webp",
             styleKey = "man_Cute_Giant"
         )
     )*/

    private fun getCuteWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Cute Hood",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/woman/Hood.webp",
            styleKey = "Cute_Hood",
            isGender = true
        ),
        CartoonStyle(
            name = "Cute Cheek",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/woman/Cheek.webp",
            styleKey = "Cute_Cheek",
            isGender = true
        ),
        CartoonStyle(
            name = "Cute Doll",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/woman/Doll.webp",
            styleKey = "Cute_Doll",
            isGender = true
        ),
        CartoonStyle(
            name = "Cute Giant",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/woman/Giant.webp",
            styleKey = "Cute_Giant",
            isGender = true
        )
    )

    // ── Reward ─────────────────────────────────────────────────────────────

    /* private fun getRewardManStyles(): List<CartoonStyle> = listOf(
         CartoonStyle(
             name = "Reward Bag",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Bag.webp",
             styleKey = "man_Reward_Bag"
         ),
         CartoonStyle(
             name = "Reward Gift",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Gift.webp",
             styleKey = "man_Reward_Gift"
         ),
         CartoonStyle(
             name = "Reward Box",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Box.webp",
             styleKey = "man_Reward_Box"
         ),
         CartoonStyle(
             name = "Reward Ornament",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Ornament.webp",
             styleKey = "man_Reward_Ornament"
         ),
         CartoonStyle(
             name = "Reward Globe",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Globe.webp",
             styleKey = "man_Reward_Globe"
         ),
         CartoonStyle(
             name = "Reward Dream",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Dream.webp",
             styleKey = "man_Reward_Dream"
         )
     )*/

    private fun getRewardWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Reward Bag",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Bag.webp",
            styleKey = "Reward_Bag",
            isGender = true
        ),
        CartoonStyle(
            name = "Reward Gift",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Gift.webp",
            styleKey = "Reward_Gift",
            isGender = true
        ),
        CartoonStyle(
            name = "Reward Box",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Box.webp",
            styleKey = "Reward_Box",
            isGender = true
        ),
        CartoonStyle(
            name = "Reward Ornament",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Ornament.webp",
            styleKey = "Reward_Ornament",
            isGender = true
        ),
        CartoonStyle(
            name = "Reward Globe",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Globe.webp",
            styleKey = "Reward_Globe",
            isGender = true
        ),
        CartoonStyle(
            name = "Reward Dream",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Dream.webp",
            styleKey = "Reward_Dream",
            isGender = true
        )
    )

    // ── Edgy ───────────────────────────────────────────────────────────────

    /*private fun getEdgyManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Edgy Caution",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Caution.webp",
            styleKey = "man_Edgy_Caution"
        ),
        CartoonStyle(
            name = "Edgy Danger",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Danger.webp",
            styleKey = "man_Edgy_Danger"
        ),
        CartoonStyle(
            name = "Edgy Film",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Film.webp",
            styleKey = "man_Edgy_Film"
        ),
        CartoonStyle(
            name = "Edgy Tapes",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Tapes.webp",
            styleKey = "man_Edgy_Tapes"
        ),
        CartoonStyle(
            name = "Edgy Chain",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Chain.webp",
            styleKey = "man_Edgy_Chain"
        ),
        CartoonStyle(
            name = "Edgy Silk",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Silk.webp",
            styleKey = "man_Edgy_Silk"
        )
    )*/

    private fun getEdgyWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Edgy Caution",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Caution.webp",
            styleKey = "Edgy_Caution",
            isGender = true
        ),
        CartoonStyle(
            name = "Edgy Danger",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Danger.webp",
            styleKey = "Edgy_Danger",
            isGender = true
        ),
        CartoonStyle(
            name = "Edgy Film",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Film.webp",
            styleKey = "Edgy_Film",
            isGender = true
        ),
        CartoonStyle(
            name = "Edgy Tapes",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Tapes.webp",
            styleKey = "Edgy_Tapes",
            isGender = true
        ),
        CartoonStyle(
            name = "Edgy Chain",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Chain.webp",
            styleKey = "Edgy_Chain",
            isGender = true
        ),
        CartoonStyle(
            name = "Edgy Silk",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Silk.webp",
            styleKey = "Edgy_Silk",
            isGender = true
        )
    )

    // ── Modern ─────────────────────────────────────────────────────────────

    /* private fun getModernManStyles(): List<CartoonStyle> = listOf(
         CartoonStyle(
             name = "Modern Cassette",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Cassette.webp",
             styleKey = "man_Modern_Cassette"
         ),
         CartoonStyle(
             name = "Modern Chess",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Chess.webp",
             styleKey = "man_Modern_Chess"
         ),
         CartoonStyle(
             name = "Modern Perfume",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Perfume.webp",
             styleKey = "man_Modern_Perfume"
         ),
         CartoonStyle(
             name = "Modern Bag",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Bag.webp",
             styleKey = "man_Modern_Bag"
         ),
         CartoonStyle(
             name = "Modern Camera",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Camera.webp",
             styleKey = "man_Modern_Camera"
         ),
         CartoonStyle(
             name = "Modern Billiard",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Billiard.webp",
             styleKey = "man_Modern_Billiard"
         )
     )*/

    private fun getModernWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Modern Cassette",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Cassette.webp",
            styleKey = "Modern_Cassette",
            isGender = true
        ),
        CartoonStyle(
            name = "Modern Chess",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Chess.webp",
            styleKey = "Modern_Chess",
            isGender = true
        ),
        CartoonStyle(
            name = "Modern Perfume",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Perfume.webp",
            styleKey = "Modern_Perfume",
            isGender = true
        ),
        CartoonStyle(
            name = "Modern Bag",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Bag.webp",
            styleKey = "Modern_Bag",
            isGender = true
        ),
        CartoonStyle(
            name = "Modern Camera",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Camera.webp",
            styleKey = "Modern_Camera",
            isGender = true
        ),
        CartoonStyle(
            name = "Modern Billiard",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Billiard.webp",
            styleKey = "Modern_Billiard",
            isGender = true
        )
    )

    // ── Cinematic ──────────────────────────────────────────────────────────

    /*   private fun getCinematicManStyles(): List<CartoonStyle> = listOf(
           CartoonStyle(
               name = "Cinematic Car",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Car.webp",
               styleKey = "man_Cinematic_Car"
           ),
           CartoonStyle(
               name = "Cinematic Train",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Train.webp",
               styleKey = "man_Cinematic_Train"
           ),
           CartoonStyle(
               name = "Cinematic Tram",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Tram.webp",
               styleKey = "man_Cinematic_Tram"
           ),
           CartoonStyle(
               name = "Cinematic Street",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Street.webp",
               styleKey = "man_Cinematic_Street"
           ),
           CartoonStyle(
               name = "Cinematic Bus",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Bus.webp",
               styleKey = "man_Cinematic_Bus"
           ),
           CartoonStyle(
               name = "Cinematic Rain",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Rain.webp",
               styleKey = "man_Cinematic_Rain"
           )
       )*/

    private fun getCinematicWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Cinematic Car",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Car.webp",
            styleKey = "Cinematic_Car",
            isGender = true
        ),
        CartoonStyle(
            name = "Cinematic Train",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Train.webp",
            styleKey = "Cinematic_Train",
            isGender = true
        ),
        CartoonStyle(
            name = "Cinematic Tram",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Tram.webp",
            styleKey = "Cinematic_Tram",
            isGender = true
        ),
        CartoonStyle(
            name = "Cinematic Street",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Street.webp",
            styleKey = "Cinematic_Street",
            isGender = true
        ),
        CartoonStyle(
            name = "Cinematic Bus",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Bus.webp",
            styleKey = "Cinematic_Bus",
            isGender = true
        ),
        CartoonStyle(
            name = "Cinematic Rain",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Rain.webp",
            styleKey = "Cinematic_Rain",
            isGender = true
        )
    )

    // ── Spotlight ──────────────────────────────────────────────────────────

    /*  private fun getSpotlightManStyles(): List<CartoonStyle> = listOf(
          CartoonStyle(
              name = "Spotlight Black",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Black.webp",
              styleKey = "man_Spotlight_Black"
          ),
          CartoonStyle(
              name = "Spotlight Blue",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Blue.webp",
              styleKey = "man_Spotlight_Blue"
          ),
          CartoonStyle(
              name = "Spotlight Red",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Red.webp",
              styleKey = "man_Spotlight_Red"
          ),
          CartoonStyle(
              name = "Spotlight Gray",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Gray.webp",
              styleKey = "man_Spotlight_Gray"
          ),
          CartoonStyle(
              name = "Spotlight Neon",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Neon.webp",
              styleKey = "man_Spotlight_Neon"
          ),
          CartoonStyle(
              name = "Spotlight Streak",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Streak.webp",
              styleKey = "man_Spotlight_Streak"
          )
      )*/

    private fun getSpotlightWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Spotlight Black",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Black.webp",
            styleKey = "Spotlight_Black",
            isGender = true
        ),
        CartoonStyle(
            name = "Spotlight Blue",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Blue.webp",
            styleKey = "Spotlight_Blue",
            isGender = true
        ),
        CartoonStyle(
            name = "Spotlight Red",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Red.webp",
            styleKey = "Spotlight_Red",
            isGender = true
        ),
        CartoonStyle(
            name = "Spotlight Gray",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Gray.webp",
            styleKey = "Spotlight_Gray",
            isGender = true
        ),
        CartoonStyle(
            name = "Spotlight Neon",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Neon.webp",
            styleKey = "Spotlight_Neon",
            isGender = true
        ),
        CartoonStyle(
            name = "Spotlight Streak",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Streak.webp",
            styleKey = "Spotlight_Streak",
            isGender = true
        )
    )

    // ── Golden ─────────────────────────────────────────────────────────────

    /* private fun getGoldenManStyles(): List<CartoonStyle> = listOf(
         CartoonStyle(
             name = "Golden Brown",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Brown.webp",
             styleKey = "man_Golden_Brown"
         ),
         CartoonStyle(
             name = "Golden Blue",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Blue.webp",
             styleKey = "man_Golden_Blue"
         ),
         CartoonStyle(
             name = "Golden Gray",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Gray.webp",
             styleKey = "man_Golden_Gray"
         ),
         CartoonStyle(
             name = "Golden Green",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Green.webp",
             styleKey = "man_Golden_Green"
         ),
         CartoonStyle(
             name = "Golden Pink",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Pink.webp",
             styleKey = "man_Golden_Pink"
         ),
         CartoonStyle(
             name = "Golden Light",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Light.webp",
             styleKey = "man_Golden_Light"
         )
     )*/

    private fun getGoldenWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Golden Brown",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Brown.webp",
            styleKey = "Golden_Brown",
            isGender = true
        ),
        CartoonStyle(
            name = "Golden Blue",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Blue.webp",
            styleKey = "Golden_Blue",
            isGender = true
        ),
        CartoonStyle(
            name = "Golden Gray",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Gray.webp",
            styleKey = "Golden_Gray",
            isGender = true
        ),
        CartoonStyle(
            name = "Golden Green",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Green.webp",
            styleKey = "Golden_Green",
            isGender = true
        ),
        CartoonStyle(
            name = "Golden Pink",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Pink.webp",
            styleKey = "Golden_Pink",
            isGender = true
        ),
        CartoonStyle(
            name = "Golden Light",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Light.webp",
            styleKey = "Golden_Light",
            isGender = true
        )
    )

    // ── Circle ─────────────────────────────────────────────────────────────

    /*  private fun getCircleManStyles(): List<CartoonStyle> = listOf(
          CartoonStyle(
              name = "Circle Red",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Red.webp",
              styleKey = "man_Circle_Red"
          ),
          CartoonStyle(
              name = "Circle Orange",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Orange.webp",
              styleKey = "man_Circle_Orange"
          ),
          CartoonStyle(
              name = "Circle Blue",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Blue.webp",
              styleKey = "man_Circle_Blue"
          ),
          CartoonStyle(
              name = "Circle Black",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Black.webp",
              styleKey = "man_Circle_Black"
          ),
          CartoonStyle(
              name = "Circle Yellow",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Yellow.webp",
              styleKey = "man_Circle_Yellow"
          ),
          CartoonStyle(
              name = "Circle Green",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Green.webp",
              styleKey = "man_Circle_Green"
          )
      )*/

    private fun getCircleWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Circle Red",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Red.webp",
            styleKey = "Circle_Red",
            isGender = true
        ),
        CartoonStyle(
            name = "Circle Orange",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Orange.webp",
            styleKey = "Circle_Orange",
            isGender = true
        ),
        CartoonStyle(
            name = "Circle Blue",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Blue.webp",
            styleKey = "Circle_Blue",
            isGender = true
        ),
        CartoonStyle(
            name = "Circle Black",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Black.webp",
            styleKey = "Circle_Black",
            isGender = true
        ),
        CartoonStyle(
            name = "Circle Yellow",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Yellow.webp",
            styleKey = "Circle_Yellow",
            isGender = true
        ),
        CartoonStyle(
            name = "Circle Green",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Green.webp",
            styleKey = "Circle_Green",
            isGender = true
        )
    )

    // ── Cube ───────────────────────────────────────────────────────────────

    /* private fun getCubeManStyles(): List<CartoonStyle> = listOf(
         CartoonStyle(
             name = "Cube Dice",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Dice.webp",
             styleKey = "man_Cube_Dice"
         ),
         CartoonStyle(
             name = "Cube Brick",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Brick.webp",
             styleKey = "man_Cube_Brick"
         ),
         CartoonStyle(
             name = "Cube Ice",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Ice.webp",
             styleKey = "man_Cube_Ice"
         ),
         CartoonStyle(
             name = "Cube Rubik",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Rubik.webp",
             styleKey = "man_Cube_Rubik"
         ),
         CartoonStyle(
             name = "Cube Television",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Television.webp",
             styleKey = "man_Cube_Television"
         ),
         CartoonStyle(
             name = "Cube Insta",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Insta.webp",
             styleKey = "man_Cube_Insta"
         )
     )*/

    private fun getCubeWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Cube Dice",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Dice.webp",
            styleKey = "Cube_Dice",
            isGender = true
        ),
        CartoonStyle(
            name = "Cube Brick",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Brick.webp",
            styleKey = "Cube_Brick",
            isGender = true
        ),
        CartoonStyle(
            name = "Cube Ice",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Ice.webp",
            styleKey = "Cube_Ice",
            isGender = true
        ),
        CartoonStyle(
            name = "Cube Rubik",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Rubik.webp",
            styleKey = "Cube_Rubik",
            isGender = true
        ),
        CartoonStyle(
            name = "Cube Television",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Television.webp",
            styleKey = "Cube_Television",
            isGender = true
        ),
        CartoonStyle(
            name = "Cube Insta",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Insta.webp",
            styleKey = "Cube_Insta",
            isGender = true
        )
    )

    // ── Studio ─────────────────────────────────────────────────────────────

    /*private fun getStudioManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Studio Jacket",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/Jacket.webp",
            styleKey = "man_Studio_Jacket"
        ),
        CartoonStyle(
            name = "Studio Black",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/Black.webp",
            styleKey = "man_Studio_Black"
        ),
        CartoonStyle(
            name = "Studio Cuboid",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/Cuboid.webp",
            styleKey = "man_Studio_Cuboid"
        ),
        CartoonStyle(
            name = "Studio White",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/White.webp",
            styleKey = "man_Studio_White"
        ),
        CartoonStyle(
            name = "Studio Table",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/Table.webp",
            styleKey = "man_Studio_Table"
        ),
        CartoonStyle(
            name = "Studio Chair",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/Chair.webp",
            styleKey = "man_Studio_Chair"
        )
    )*/

    private fun getStudioWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Studio Jacket",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/Jacket.webp",
            styleKey = "Studio_Jacket",
            isGender = true
        ),
        CartoonStyle(
            name = "Studio Black",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/Black.webp",
            styleKey = "Studio_Black",
            isGender = true
        ),
        CartoonStyle(
            name = "Studio Cuboid",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/Cuboid.webp",
            styleKey = "Studio_Cuboid",
            isGender = true
        ),
        CartoonStyle(
            name = "Studio White",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/White.webp",
            styleKey = "Studio_White",
            isGender = true
        ),
        CartoonStyle(
            name = "Studio Table",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/Table.webp",
            styleKey = "Studio_Table",
            isGender = true
        ),
        CartoonStyle(
            name = "Studio Chair",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/Chair.webp",
            styleKey = "Studio_Chair",
            isGender = true
        )
    )

    // ── Bwstudio ───────────────────────────────────────────────────────────

    /*  private fun getBwstudioManStyles(): List<CartoonStyle> = listOf(
          CartoonStyle(
              name = "Bwstudio Studio",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Studio.webp",
              styleKey = "man_Bwstudio_Studio"
          ),
          CartoonStyle(
              name = "Bwstudio Fence",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Fence.webp",
              styleKey = "man_Bwstudio_Fence"
          ),
          CartoonStyle(
              name = "Bwstudio Car",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Car.webp",
              styleKey = "man_Bwstudio_Car"
          ),
          CartoonStyle(
              name = "Bwstudio Umbrella",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Umbrella.webp",
              styleKey = "man_Bwstudio_Umbrella"
          ),
          CartoonStyle(
              name = "Bwstudio Field",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Field.webp",
              styleKey = "man_Bwstudio_Field"
          ),
          CartoonStyle(
              name = "Bwstudio Wall",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Wall.webp",
              styleKey = "man_Bwstudio_Wall"
          )
      )*/

    private fun getBwstudioWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Bwstudio Studio",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Studio.webp",
            styleKey = "Bwstudio_Studio",
            isGender = true
        ),
        CartoonStyle(
            name = "Bwstudio Fence",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Fence.webp",
            styleKey = "Bwstudio_Fence",
            isGender = true
        ),
        CartoonStyle(
            name = "Bwstudio Car",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Car.webp",
            styleKey = "Bwstudio_Car",
            isGender = true
        ),
        CartoonStyle(
            name = "Bwstudio Umbrella",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Umbrella.webp",
            styleKey = "Bwstudio_Umbrella",
            isGender = true
        ),
        CartoonStyle(
            name = "Bwstudio Field",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Field.webp",
            styleKey = "Bwstudio_Field",
            isGender = true
        ),
        CartoonStyle(
            name = "Bwstudio Wall",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Wall.webp",
            styleKey = "Bwstudio_Wall",
            isGender = true
        )
    )

    // ── Monochromatic ──────────────────────────────────────────────────────

    /* private fun getMonochromaticManStyles(): List<CartoonStyle> = listOf(
         CartoonStyle(
             name = "Monochromatic Orange",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/Orange.webp",
             styleKey = "man_Monochromatic_Orange"
         ),
         CartoonStyle(
             name = "Monochromatic Gold",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/Gold.webp",
             styleKey = "man_Monochromatic_Gold"
         ),
         CartoonStyle(
             name = "Monochromatic Purple",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/Purple.webp",
             styleKey = "man_Monochromatic_Purple"
         ),
         CartoonStyle(
             name = "Monochromatic Red",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/Red.webp",
             styleKey = "man_Monochromatic_Red"
         ),
         CartoonStyle(
             name = "Monochromatic Blue",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/Blue.webp",
             styleKey = "man_Monochromatic_Blue"
         ),
         CartoonStyle(
             name = "Monochromatic White",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/White.webp",
             styleKey = "man_Monochromatic_White"
         )
     )*/

    private fun getMonochromaticWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Monochromatic Orange",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/Orange.webp",
            styleKey = "Monochromatic_Orange",
            isGender = true
        ),
        CartoonStyle(
            name = "Monochromatic Gold",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/Gold.webp",
            styleKey = "Monochromatic_Gold",
            isGender = true
        ),
        CartoonStyle(
            name = "Monochromatic Purple",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/Purple.webp",
            styleKey = "Monochromatic_Purple",
            isGender = true
        ),
        CartoonStyle(
            name = "Monochromatic Red",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/Red.webp",
            styleKey = "Monochromatic_Red",
            isGender = true
        ),
        CartoonStyle(
            name = "Monochromatic Blue",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/Blue.webp",
            styleKey = "Monochromatic_Blue",
            isGender = true
        ),
        CartoonStyle(
            name = "Monochromatic White",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/White.webp",
            styleKey = "Monochromatic_White",
            isGender = true
        )
    )

    // ── LinkedIn ───────────────────────────────────────────────────────────

    /*   private fun getLinkedInManStyles(): List<CartoonStyle> = listOf(
           CartoonStyle(
               name = "LinkedIn Pistachio",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/Pistachio.webp",
               styleKey = "man_LinkedIn_Pistachio"
           ),
           CartoonStyle(
               name = "LinkedIn Black",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/Black.webp",
               styleKey = "man_LinkedIn_Black"
           ),
           CartoonStyle(
               name = "LinkedIn White",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/White.webp",
               styleKey = "man_LinkedIn_White"
           ),
           CartoonStyle(
               name = "LinkedIn Navy",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/Navy.webp",
               styleKey = "man_LinkedIn_Navy"
           ),
           CartoonStyle(
               name = "LinkedIn Beige",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/Beige.webp",
               styleKey = "man_LinkedIn_Beige"
           ),
           CartoonStyle(
               name = "LinkedIn Grey",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/Grey.webp",
               styleKey = "man_LinkedIn_Grey"
           )
       )*/

    private fun getLinkedInWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "LinkedIn Pistachio",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/Pistachio.webp",
            styleKey = "LinkedIn_Pistachio",
            isGender = true
        ),
        CartoonStyle(
            name = "LinkedIn Black",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/Black.webp",
            styleKey = "LinkedIn_Black",
            isGender = true
        ),
        CartoonStyle(
            name = "LinkedIn White",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/White.webp",
            styleKey = "LinkedIn_White",
            isGender = true
        ),
        CartoonStyle(
            name = "LinkedIn Navy",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/Navy.webp",
            styleKey = "LinkedIn_Navy",
            isGender = true
        ),
        CartoonStyle(
            name = "LinkedIn Beige",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/Beige.webp",
            styleKey = "LinkedIn_Beige",
            isGender = true
        ),
        CartoonStyle(
            name = "LinkedIn Grey",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/Grey.webp",
            styleKey = "LinkedIn_Grey",
            isGender = true
        )
    )

    // ── Suit ───────────────────────────────────────────────────────────────

    /* private fun getSuitManStyles(): List<CartoonStyle> = listOf(
         CartoonStyle(
             name = "Suit Pistachio",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Pistachio.webp",
             styleKey = "man_Suit_Pistachio"
         ),
         CartoonStyle(
             name = "Suit Black",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Black.webp",
             styleKey = "man_Suit_Black"
         ),
         CartoonStyle(
             name = "Suit Beige",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Beige.webp",
             styleKey = "man_Suit_Beige"
         ),
         CartoonStyle(
             name = "Suit Navy",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Navy.webp",
             styleKey = "man_Suit_Navy"
         ),
         CartoonStyle(
             name = "Suit Grey",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Grey.webp",
             styleKey = "man_Suit_Grey"
         ),
         CartoonStyle(
             name = "Suit Brown",
             iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Brown.webp",
             styleKey = "man_Suit_Brown"
         )
     )*/

    private fun getSuitWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Suit Pistachio",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Pistachio.webp",
            styleKey = "Suit_Pistachio",
            isGender = true
        ),
        CartoonStyle(
            name = "Suit Black",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Black.webp",
            styleKey = "Suit_Black",
            isGender = true
        ),
        CartoonStyle(
            name = "Suit Beige",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Beige.webp",
            styleKey = "Suit_Beige",
            isGender = true
        ),
        CartoonStyle(
            name = "Suit Navy",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Navy.webp",
            styleKey = "Suit_Navy",
            isGender = true
        ),
        CartoonStyle(
            name = "Suit Grey",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Grey.webp",
            styleKey = "Suit_Grey",
            isGender = true
        ),
        CartoonStyle(
            name = "Suit Brown",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Brown.webp",
            styleKey = "Suit_Brown",
            isGender = true
        )
    )

    // ── Christmas ──────────────────────────────────────────────────────────

    /*  private fun getChristmasManStyles(): List<CartoonStyle> = listOf(
          CartoonStyle(
              name = "Christmas Red",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/Red.webp",
              styleKey = "man_Christmas_Red"
          ),
          CartoonStyle(
              name = "Christmas White",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/White.webp",
              styleKey = "man_Christmas_White"
          ),
          CartoonStyle(
              name = "Christmas Green",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/Green.webp",
              styleKey = "man_Christmas_Green"
          ),
          CartoonStyle(
              name = "Christmas Gold",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/Gold.webp",
              styleKey = "man_Christmas_Gold"
          ),
          CartoonStyle(
              name = "Christmas Burgundy",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/Burgundy.webp",
              styleKey = "man_Christmas_Burgundy"
          ),
          CartoonStyle(
              name = "Christmas Silver",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/Silver.webp",
              styleKey = "man_Christmas_Silver"
          )
      )*/

    private fun getChristmasWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Christmas Red",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/Red.webp",
            styleKey = "Christmas_Red",
            isGender = true
        ),
        CartoonStyle(
            name = "Christmas White",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/White.webp",
            styleKey = "Christmas_White",
            isGender = true
        ),
        CartoonStyle(
            name = "Christmas Green",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/Green.webp",
            styleKey = "Christmas_Green",
            isGender = true
        ),
        CartoonStyle(
            name = "Christmas Gold",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/Gold.webp",
            styleKey = "Christmas_Gold",
            isGender = true
        ),
        CartoonStyle(
            name = "Christmas Burgundy",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/Burgundy.webp",
            styleKey = "Christmas_Burgundy",
            isGender = true
        ),
        CartoonStyle(
            name = "Christmas Silver",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/Silver.webp",
            styleKey = "Christmas_Silver",
            isGender = true
        )
    )

    // ── BabyChristmas ──────────────────────────────────────────────────────

    /*  private fun getBabyChristmasManStyles(): List<CartoonStyle> = listOf(
          CartoonStyle(
              name = "BabyChristmas Cozy",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Cozy.webp",
              styleKey = "man_BabyChristmas_Cozy"
          ),
          CartoonStyle(
              name = "BabyChristmas Winter",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Winter.webp",
              styleKey = "man_BabyChristmas_Winter"
          ),
          CartoonStyle(
              name = "BabyChristmas Tiny",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Tiny.webp",
              styleKey = "man_BabyChristmas_Tiny"
          ),
          CartoonStyle(
              name = "BabyChristmas Sweet",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Sweet.webp",
              styleKey = "man_BabyChristmas_Sweet"
          ),
          CartoonStyle(
              name = "BabyChristmas Dream",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Dream.webp",
              styleKey = "man_BabyChristmas_Dream"
          ),
          CartoonStyle(
              name = "BabyChristmas Soft",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Soft.webp",
              styleKey = "man_BabyChristmas_Soft"
          )
      )*/

    private fun getBabyChristmasWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "BabyChristmas Cozy",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Cozy.webp",
            styleKey = "BabyChristmas_Cozy",
            isGender = true
        ),
        CartoonStyle(
            name = "BabyChristmas Winter",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Winter.webp",
            styleKey = "BabyChristmas_Winter",
            isGender = true
        ),
        CartoonStyle(
            name = "BabyChristmas Tiny",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Tiny.webp",
            styleKey = "BabyChristmas_Tiny",
            isGender = true
        ),
        CartoonStyle(
            name = "BabyChristmas Sweet",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Sweet.webp",
            styleKey = "BabyChristmas_Sweet",
            isGender = true
        ),
        CartoonStyle(
            name = "BabyChristmas Dream",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Dream.webp",
            styleKey = "BabyChristmas_Dream",
            isGender = true
        ),
        CartoonStyle(
            name = "BabyChristmas Soft",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Soft.webp",
            styleKey = "BabyChristmas_Soft",
            isGender = true
        )
    )


    // ── Lunar ──────────────────────────────────────────────────────────────

    /*  private fun getLunarManStyles(): List<CartoonStyle> = listOf(
          CartoonStyle(
              name = "Lunar Lion",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Lion.webp",
              styleKey = "man_Lunar_Lion"
          ),
          CartoonStyle(
              name = "Lunar Greeting",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Greeting.webp",
              styleKey = "man_Lunar_Greeting"
          ),
          CartoonStyle(
              name = "Lunar Envelopes",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Envelopes.webp",
              styleKey = "man_Lunar_Envelopes"
          ),
          CartoonStyle(
              name = "Lunar Heritage",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Heritage.webp",
              styleKey = "man_Lunar_Heritage"
          ),
          CartoonStyle(
              name = "Lunar Porcelain",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Porcelain.webp",
              styleKey = "man_Lunar_Porcelain"
          ),
          CartoonStyle(
              name = "Lunar Fortune",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Fortune.webp",
              styleKey = "man_Lunar_Fortune"
          )
      )*/


    private fun getLunarWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Lunar Lion",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Lion.webp",
            styleKey = "Lunar_Lion",
            isGender = true
        ),
        CartoonStyle(
            name = "Lunar Greeting",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Greeting.webp",
            styleKey = "Lunar_Greeting",
            isGender = true
        ),
        CartoonStyle(
            name = "Lunar Envelopes",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Envelopes.webp",
            styleKey = "Lunar_Envelopes",
            isGender = true
        ),
        CartoonStyle(
            name = "Lunar Heritage",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Heritage.webp",
            styleKey = "Lunar_Heritage",
            isGender = true
        ),
        CartoonStyle(
            name = "Lunar Porcelain",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Porcelain.webp",
            styleKey = "Lunar_Porcelain",
            isGender = true
        ),
        CartoonStyle(
            name = "Lunar Fortune",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Fortune.webp",
            styleKey = "Lunar_Fortune",
            isGender = true
        )
    )


    // ── VNLuna ─────────────────────────────────────────────────────────────

    /*  private fun getVNLunaManStyles(): List<CartoonStyle> = listOf(
          CartoonStyle(
              name = "VNLuna Banhchung",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Banhchung.webp",
              styleKey = "man_VNLuna_Banhchung"
          ),
          CartoonStyle(
              name = "VNLuna Mai",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Mai.webp",
              styleKey = "man_VNLuna_Mai"
          ),
          CartoonStyle(
              name = "VNLuna Dao",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Dao.webp",
              styleKey = "man_VNLuna_Dao"
          ),
          CartoonStyle(
              name = "VNLuna Candy",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Candy.webp",
              styleKey = "man_VNLuna_Candy"
          ),
          CartoonStyle(
              name = "VNLuna Lucky",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Lucky.webp",
              styleKey = "man_VNLuna_Lucky"
          ),
          CartoonStyle(
              name = "VNLuna Horse",
              iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Horse.webp",
              styleKey = "man_VNLuna_Horse"
          )
      )*/

    private fun getVNLunaWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "VNLuna Banhchung",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Banhchung.webp",
            styleKey = "VNLuna_Banhchung",
            isGender = true
        ),
        CartoonStyle(
            name = "VNLuna Mai",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Mai.webp",
            styleKey = "VNLuna_Mai",
            isGender = true
        ),
        CartoonStyle(
            name = "VNLuna Dao",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Dao.webp",
            styleKey = "VNLuna_Dao",
            isGender = true
        ),
        CartoonStyle(
            name = "VNLuna Candy",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Candy.webp",
            styleKey = "VNLuna_Candy",
            isGender = true
        ),
        CartoonStyle(
            name = "VNLuna Lucky",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Lucky.webp",
            styleKey = "VNLuna_Lucky",
            isGender = true
        ),
        CartoonStyle(
            name = "VNLuna Horse",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Horse.webp",
            styleKey = "VNLuna_Horse",
            isGender = true
        )
    )


    // ── Aging ──────────────────────────────────────────────────────────────

    /* private fun getAgingManStyles(): List<CartoonStyle> = listOf(
         CartoonStyle(
             name = "Aging 20",
             iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_man_aging20_1779641724595.jpg",
             styleKey = "man_aging20"
         ),
         CartoonStyle(
             name = "Aging 30",
             iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_man_aging30_1779641050451.jpg",
             styleKey = "man_aging30"
         ),
         CartoonStyle(
             name = "Aging 40",
             iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_man_aging40_1779641846047.jpg",
             styleKey = "man_aging40"
         ),
         CartoonStyle(
             name = "Aging 50",
             iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_man_aging50_1779641966107.jpg",
             styleKey = "man_aging50"
         ),
         CartoonStyle(
             name = "Aging 60",
             iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_man_aging60_1779642117904.jpg",
             styleKey = "man_aging60"
         ),
         CartoonStyle(
             name = "Aging 70",
             iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_man_aging70_1779642205634.jpg",
             styleKey = "man_aging70"
         ),
         CartoonStyle(
             name = "Aging 80",
             iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_man_aging80_1779642337986.jpg",
             styleKey = "man_aging80"
         )
     )*/


    private fun getAgingWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Aging 20",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_woman_aging20_1779641267139.jpg",
            styleKey = "aging20",
            isGender = true
        ),
        CartoonStyle(
            name = "Aging 30",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_woman_aging30_1779641067037.jpg",
            styleKey = "aging30",
            isGender = true
        ),
        CartoonStyle(
            name = "Aging 40",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_woman_aging40_1779641763429.jpg",
            styleKey = "aging40",
            isGender = true
        ),
        CartoonStyle(
            name = "Aging 50",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_woman_aging50_1779642602648.jpg",
            styleKey = "aging50",
            isGender = true
        ),
        CartoonStyle(
            name = "Aging 60",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_woman_aging50_1779642195578.jpg",
            styleKey = "aging60",
            isGender = true
        ),
        CartoonStyle(
            name = "Aging 70",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_woman_aging70_1779642276014.jpg",
            styleKey = "aging70",
            isGender = true
        ),
        CartoonStyle(
            name = "Aging 80",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_woman_aging80_1779642386051.jpg",
            styleKey = "aging80",
            isGender = true
        )
    )
    // ── Haircut Styles ─────────────────────────────────────────────────────

    private fun getHaircutStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Bod",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_haircut_bod_1779639501728.jpg",
            styleKey = "haircut_bod"
        ),
        CartoonStyle(
            name = "Curly",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "haircut_curly"
        ),
        CartoonStyle(
            name = "Straight",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "haircut_straight"
        ),
        CartoonStyle(
            name = "Wavy",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "haircut_wavy"
        ),
        CartoonStyle(
            name = "Panytail",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "haircut_ponytail"
        ),
        CartoonStyle(
            name = "Short Box",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "haircut_shortbox"
        ),
        CartoonStyle(
            name = "Curly Pixie",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "haircut_curlypixie"
        ),
        CartoonStyle(
            name = "Wispy Layer",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "haircut_wispy_layer"
        ),
        CartoonStyle(
            name = "Low Bun",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "haircut_lowbun"
        ),
        CartoonStyle(
            name = "French Braid",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "haircut_frenchbraid"
        ),
        CartoonStyle(
            name = "Three Strand",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "haircut_threestrand"
        )
    )

// ── Figurine & Art Styles ───────────────────────────────────────────────

    private fun getFigurineStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Figurine 02",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_figurine02_1779634146222.jpg",
            styleKey = "figurine02"
        ),
        CartoonStyle(
            name = "Ghibli",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_ghibli_runninghub_1946470668230619137_1779591514733.jpg",
            styleKey = "ghibli_runninghub_1946470668230619137"
        ),
        CartoonStyle(
            name = "3D Blind Box",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_3dblindbox_1779634183561.jpg",
            styleKey = "3dblindbox"
        ),
        CartoonStyle(
            name = "3D Chibi Cute",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "3dchibicute"
        ),
        CartoonStyle(
            name = "PVC Figure",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_pvc_figure_1779634799746.jpg",
            styleKey = "pvc_figure"
        ),
        CartoonStyle(
            name = "GTA 5",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_gta5_1779627039461.jpg",
            styleKey = "gta5"
        ),
        CartoonStyle(
            name = "Retro Old Movies",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_retro_style_old_movies_1779639200221.jpg",
            styleKey = "retro_style_old_movies"
        ),
        CartoonStyle(
            name = "Pop Art",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "pop_art"
        ),
        CartoonStyle(
            name = "Comic Lineart",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_comic_lineart_1779633216134.jpg",
            styleKey = "comic_lineart"
        ),
        CartoonStyle(
            name = "Pixel 2",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_pixel2_1779637036213.jpg",
            styleKey = "pixel2"
        ),
        CartoonStyle(
            name = "Pixel 1",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "pixel1"
        ),
        CartoonStyle(
            name = "Animesh Flower",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_animeshflower_1779637414545.jpg",
            styleKey = "animeshflower"
        ),
        CartoonStyle(
            name = "Simpsons",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_simpsons_1779638756793.jpg",
            styleKey = "simpsons"
        ),
        CartoonStyle(
            name = "Chibi Sunflower",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_chibisunflower_1779634945383.jpg",
            styleKey = "chibisunflower"
        ),
        CartoonStyle(
            name = "Animesh Flat",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_animesh_flat_1779637721293.jpg",
            styleKey = "animesh_flat"
        ),
        CartoonStyle(
            name = "GTA 5 Artwork",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_gta5_artwork_1779627663537.jpg",
            styleKey = "gta5_artwork"
        ),
        CartoonStyle(
            name = "Toonify",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_toonify_1779637854004.jpg",
            styleKey = "toonify"
        )
    )

    private fun getAIPhotoCartoonStyles(): List<CartoonStyle> = listOf(
        /*  CartoonStyle(
              name = "Ghibli",
              iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_ghibli_runninghub_1946470668230619137_1779591514733.jpg",
              styleKey = "ghibli_runninghub_1946470668230619137"
          ),*/
        CartoonStyle(
            name = "Anime Style",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "anime"
        ),
        CartoonStyle(
            name = "Retro Style",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_retro_style_old_movies_1779636813985.jpg",
            styleKey = "retro_style_old_movies"
        ),
        CartoonStyle(
            name = "Toonify 2",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_toonify2_1779636947515.jpg",
            styleKey = "toonify2"
        ),
        CartoonStyle(
            name = "Pixel 2",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_pixel2_1779637036213.jpg",
            styleKey = "pixel2"
        ),
        CartoonStyle(
            name = "Pixel 1",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_pixel_1779637219147.jpg",
            styleKey = "pixel"
        ),
        CartoonStyle(
            name = "Animesh Flower",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_animeshflower_1779637414545.jpg",
            styleKey = "animeshflower"
        ),
        CartoonStyle(
            name = "Anime Flat",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_animesh_flat_1779637721293.jpg",
            styleKey = "animesh_flat"
        ),
        CartoonStyle(
            name = "Toonify",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_toonify_1779637854004.jpg",
            styleKey = "toonify"
        ),
        CartoonStyle(
            name = "Mistoon Anime",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_mistoon_anime_1779638024531.jpg",
            styleKey = "mistoon_anime"
        ),
        CartoonStyle(
            name = "Pastel",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_pastel_1779638067945.jpg",
            styleKey = "pastel"
        ),
        CartoonStyle(
            name = "Manmaru Mix",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_manmarumix_1779638128168.jpg",
            styleKey = "manmarumix"
        ),
        CartoonStyle(
            name = "Simpsons",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_simpsons_1779638756793.jpg",
            styleKey = "simpsons"
        )
    )


    private fun get3DCartoonStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Figurine 3D",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_figurine02_1779634146222.jpg",
            styleKey = "figurine02"
        ),
        CartoonStyle(
            name = "3D Blind Box",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_3dblindbox_1779634183561.jpg",
            styleKey = "3dblindbox"
        ),
        CartoonStyle(
            name = "Chibi Cute",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_chibi_cute_1779634580845.jpg",
            styleKey = "chibi_cute"
        ),
        CartoonStyle(
            name = "3D Cartoon",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_cartoon3d2_1779638965798.jpg",
            styleKey = "cartoon3d2"
        ),
        CartoonStyle(
            name = "PVC Figure",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_pvc_figure_1779634799746.jpg",
            styleKey = "pvc_figure"
        ),
        CartoonStyle(
            name = "Chibi Sunflower",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_chibisunflower_1779634945383.jpg",
            styleKey = "chibisunflower"
        ),
        CartoonStyle(
            name = "Spring Corner",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_springcorner_1779635071797.jpg",
            styleKey = "springcorner"
        ),
        CartoonStyle(
            name = "Toonify Chibi",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_toonifychibi_1779635195352.jpg",
            styleKey = "toonifychibi"
        ),
        CartoonStyle(
            name = "Chibi Space",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_chibispace_1779635241045.jpg",
            styleKey = "chibispace"
        )
    )

    private fun getComicStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Dragon Ball",
            iconUrl = "https://iili.io/C2pjDiB.md.png",
            styleKey = "dragon_ball"
        ),
        CartoonStyle(
            name = "Comic Lineart",
            iconUrl = "https://iili.io/C2pNaQR.md.jpg",
            styleKey = "comic_lineart"
        ),
        CartoonStyle(
            name = "Comic Cartoonish",
            iconUrl = "https://iili.io/C2pSU8B.md.jpg",
            styleKey = "comic_cartoonish"
        ),
        CartoonStyle(
            name = "Comic Sketch",
            iconUrl = "https://iili.io/C2pUXlR.jpg",
            styleKey = "comic_sketch"
        ),
        CartoonStyle(
            name = "Comic Flat",
            iconUrl = "https://iili.io/C2pbkYX.jpg",
            styleKey = "comic_flat"
        ),
        CartoonStyle(
            name = "Comic Sepia",
            iconUrl = "https://iili.io/C2pDgqB.jpg",
            styleKey = "comic_sepia"
        ),
        CartoonStyle(
            name = "Comic 2",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_comic2_1779633782125.jpg",
            styleKey = "comic2"
        )
    )

    private fun getToonmixStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "GTA V",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_gta5_1779627039461.jpg",
            styleKey = "gta5"
        ),
        /*   CartoonStyle(
               name = "Game Style",
               iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
               styleKey = "ylt_game_character_design"
           ),*/
        CartoonStyle(
            name = "Cute Painting",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_cutepainting_1779627464194.jpg",
            styleKey = "cutepainting"
        ),
        CartoonStyle(
            name = "Clay World",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_3d_cartoon_clay_1779627588235.jpg",
            styleKey = "3d_cartoon_clay"
        ),
        CartoonStyle(
            name = "GTA 5 Artwork",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_gta5_artwork_1779627663537.jpg",
            styleKey = "gta5_artwork"
        ),
        CartoonStyle(
            name = "Animesh",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_animesh_1779627947985.jpg",
            styleKey = "animesh"
        ),
        CartoonStyle(
            name = "Wool Felt",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_woolfelt_1779628274251.jpg",
            styleKey = "woolfelt"
        ),
        CartoonStyle(
            name = "Elf",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_elf_1779628377175.jpg",
            styleKey = "elf"
        ),
        CartoonStyle(
            name = "Toon Mix",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_toonmix_1779628487106.jpg",
            styleKey = "toonmix"
        ),
        CartoonStyle(
            name = "Snow White",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_snow_white_1779628585494.jpg",
            styleKey = "snow_white"
        ),
        CartoonStyle(
            name = "Majic Mix Lux",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_majicmixlux_1779628739361.jpg",
            styleKey = "majicmixlux"
        ),
        CartoonStyle(
            name = "Toon You",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_toonyou_1779628849767.jpg",
            styleKey = "toonyou"
        ),
        CartoonStyle(
            name = "Cartoon Vision",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_cartoonvision_1779628970979.jpg",
            styleKey = "cartoonvision"
        ),
        CartoonStyle(
            name = "Meina Mix",
            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",
            styleKey = "Meinamix"
        )
    )

    private fun getIllustrationStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(
            name = "Pop Art",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_pop_art_1779735227242.jpg",
            styleKey = "pop_art"
        ),
        CartoonStyle(
            name = "Flat Children Illustrations",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_flat_children_illustrations_1779619019582.jpg",
            styleKey = "flat_children_illustrations"
        ),
        CartoonStyle(
            name = "Dreamy Watercolor",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_dreamy_watercolor_1779735373884.jpg",
            styleKey = "dreamy_watercolor"
        ),
        CartoonStyle(
            name = "Brush Strokes",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_brush_strokes_1779619914657.jpg",
            styleKey = "brush_strokes"
        ),
        CartoonStyle(
            name = "Flat Illustration Style",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_flat_illustration_style_1779734778205.jpg",
            styleKey = "flat_illustration_style"
        ),
        CartoonStyle(
            name = "Water Anime",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_wateranime_1779620134486.jpg",
            styleKey = "wateranime"
        ),
        CartoonStyle(
            name = "Illustration",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_illustration_1779620521928.jpg",
            styleKey = "illustration"
        ),
        CartoonStyle(
            name = "Monet Oil Painting",
            iconUrl = "https://api.photoshoot.zeezoo.mobi/aiphoto_cartoon_monet_oil_painting_v1_1779735059303.jpg",
            styleKey = "monet_oil_painting_v1"
        )
    )

    private fun initActivityTool() {
        val currentRecycleTool = recycleTool ?: return

        val homeToolAdapter = HomeToolAdapter()

        // Extract data utilizing your native utility implementation logic
        val toolList = ToolEnhanceUtils.getListToolEnhance(this)
        homeToolAdapter.setDataList(toolList)

        // Define functional item click routing via anonymous mapping functions
        homeToolAdapter.setOnClickItem { toolEnhance, positionWrapper ->
            var featureString = ""
            // Scenario A: Check explicitly for target old photo restoration functionality
            /*if (Intrinsics.areEqual(toolEnhance != null ? toolEnhance.getFeature() : null, Feature.RESTORE_OLD_PHOTO.getValue())) {
                val intent = Intent(HomeActivity.this, IntroRestoreOldPhotoActivity.class)
                safedk_HomeActivity_startActivity(HomeActivity.this, intent)

                val bundle = Bundle()
                return@setOnClickItem
            }*/

            // Scenario B: Fall back safely into standard modular photo library routing structures
            val intent = Intent(this, LibraryVer2Activity::class.java)
            if (toolEnhance == null || toolEnhance.feature
                    .also { featureString = it } == null
            ) {
                featureString = "home_error"
            }

            intent.putExtra(FEATURE, featureString)
            safedk_HomeActivity_startActivity(this, intent)

            Bundle()
            toolEnhance?.feature

            Log.e("toolEnhance", "initActivityTool: " + toolEnhance.feature)

        }

        // Bind fully operational tool layout down to interface layer
        currentRecycleTool.adapter = homeToolAdapter
    }

    companion object {
        const val FEATURE = "feature"

        // SafeDK wrapper tracking mimicking your architecture requirements
        @JvmStatic
        fun safedk_HomeActivity_startActivity(activity: MainActivity?, intent: Intent?) {
            if (intent == null || activity == null) return
            activity.startActivity(intent)
        }
    }
}