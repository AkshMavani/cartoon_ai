package com.skylock.ai_cartoon.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.skylock.ai_cartoon.R
import com.skylock.ai_cartoon.fragment.CartoonStyleFragment
import com.skylock.ai_cartoon.model.CartoonStyle

class MainActivity : AppCompatActivity() {

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
    }

    private fun setupTabLayout() {
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        val tabs = listOf(
            "All"              to getAllStyles(),
            "Cartoon"          to getCartoonStyles(),
            "Hair"             to getHairStyles(),
            "Birthday Man"     to getBirthdayManStyles(),
            "Birthday Woman"   to getBirthdayWomanStyles(),
            "Valentine Man"    to getValentineManStyles(),
            "Valentine Woman"  to getValentineWomanStyles(),
            "Winter Man"       to getWinterManStyles(),
            "Winter Woman"     to getWinterWomanStyles(),
            "Newyear Man"      to getNewyearManStyles(),
            "Newyear Woman"    to getNewyearWomanStyles(),
            "Cute Man"         to getCuteManStyles(),
            "Cute Woman"       to getCuteWomanStyles(),
            "Reward Man"       to getRewardManStyles(),
            "Reward Woman"     to getRewardWomanStyles(),
            "Edgy Man"         to getEdgyManStyles(),
            "Edgy Woman"       to getEdgyWomanStyles(),
            "Modern Man"       to getModernManStyles(),
            "Modern Woman"     to getModernWomanStyles(),
            "Cinematic Man"    to getCinematicManStyles(),
            "Cinematic Woman"  to getCinematicWomanStyles(),
            "Spotlight Man"    to getSpotlightManStyles(),
            "Spotlight Woman"  to getSpotlightWomanStyles(),
            "Golden Man"       to getGoldenManStyles(),
            "Golden Woman"     to getGoldenWomanStyles(),
            "Circle Man"       to getCircleManStyles(),
            "Circle Woman"     to getCircleWomanStyles(),
            "Cube Man"         to getCubeManStyles(),
            "Cube Woman"       to getCubeWomanStyles(),
            "Studio Man"       to getStudioManStyles(),
            "Studio Woman"     to getStudioWomanStyles(),
            "Bwstudio Man"     to getBwstudioManStyles(),
            "Bwstudio Woman"   to getBwstudioWomanStyles(),
            "Monochromatic Man"   to getMonochromaticManStyles(),
            "Monochromatic Woman" to getMonochromaticWomanStyles(),
            "LinkedIn Man"     to getLinkedInManStyles(),
            "LinkedIn Woman"   to getLinkedInWomanStyles(),
            "Suit Man"         to getSuitManStyles(),
            "Suit Woman"       to getSuitWomanStyles(),
            "Christmas Man"    to getChristmasManStyles(),
            "Christmas Woman"  to getChristmasWomanStyles(),
            "BabyChristmas Man"   to getBabyChristmasManStyles(),
            "BabyChristmas Woman" to getBabyChristmasWomanStyles(),
            "Lunar Man"        to getLunarManStyles(),
            "Lunar Woman"      to getLunarWomanStyles(),
            "VNLuna Man"       to getVNLunaManStyles(),
            "VNLuna Woman"     to getVNLunaWomanStyles(),
            "Aging Man"        to getAgingManStyles(),    // <-- ADD
            "Aging Woman"      to getAgingWomanStyles()  ,
            "Haircut"          to getHaircutStyles(),    // <-- ADD
            "Figurine & Art"   to getFigurineStyles()    // <-- ADD
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

    fun goAIAvatar(style: String, feature: String, isPremiumItem: Boolean) {
        val intent = Intent(this, CartoonIntroActivity::class.java).apply {
            putExtra("style", style)
            putExtra("feature", feature)
            putExtra("is_premium_item", isPremiumItem)
        }
        startActivity(intent)
    }

    // ── Data ──────────────────────────────────────────────────────────────

    private fun getAllStyles(): List<CartoonStyle> =
        getCartoonStyles() + getHairStyles() +
                getBirthdayManStyles() + getBirthdayWomanStyles() +
                getValentineManStyles() + getValentineWomanStyles() +
                getWinterManStyles() + getWinterWomanStyles() +
                getNewyearManStyles() + getNewyearWomanStyles() +
                getCuteManStyles() + getCuteWomanStyles() +
                getRewardManStyles() + getRewardWomanStyles() +
                getEdgyManStyles() + getEdgyWomanStyles() +
                getModernManStyles() + getModernWomanStyles() +
                getCinematicManStyles() + getCinematicWomanStyles() +
                getSpotlightManStyles() + getSpotlightWomanStyles() +
                getGoldenManStyles() + getGoldenWomanStyles() +
                getCircleManStyles() + getCircleWomanStyles() +
                getCubeManStyles() + getCubeWomanStyles() +
                getStudioManStyles() + getStudioWomanStyles() +
                getBwstudioManStyles() + getBwstudioWomanStyles() +
                getMonochromaticManStyles() + getMonochromaticWomanStyles() +
                getLinkedInManStyles() + getLinkedInWomanStyles() +
                getSuitManStyles() + getSuitWomanStyles() +
                getChristmasManStyles() + getChristmasWomanStyles() +
                getBabyChristmasManStyles() + getBabyChristmasWomanStyles() +
                getLunarManStyles() + getLunarWomanStyles() +
                getVNLunaManStyles() + getVNLunaWomanStyles()+
                getAgingManStyles() + getAgingWomanStyles()+
                getHaircutStyles() + getFigurineStyles()

    // ── Cartoon & Hair (unchanged) ─────────────────────────────────────────

    private fun getCartoonStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Ghibli",            iconUrl = "https://res.zeezoo.mobi/cartoon2/styles/01new/ghibli/ghibli_icon01.png",       styleKey = "ghibli_runninghub_1946470668230619137"),
        CartoonStyle(name = "3D Emoji",          iconUrl = "https://res.zeezoo.mobi/cartoon2/styles/01new/3demoji/icon.png",                styleKey = "3demoji"),
        CartoonStyle(name = "GPT-4o Travel",     iconUrl = "https://res.zeezoo.mobi/cartoon2/styles/gpt4o_travel/travel_icon.jpg",          styleKey = "polaroid"),
        CartoonStyle(name = "GPT-4o Best Friend",iconUrl = "https://res.zeezoo.mobi/cartoon2/styles/gpt4o_bestfriend/gpt4o_bestfriends_icon.jpg", styleKey = "polaroid2"),
        CartoonStyle(name = "GPT-4o Summer",     iconUrl = "https://res.zeezoo.mobi/cartoon2/styles/gpt4o_summer/icon.jpg",                styleKey = "polaroid3summer"),
        CartoonStyle(name = "Qwen 3D Chibi",     iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwen3dchibi/icon.webp",      styleKey = "qwen3dchibi"),
        CartoonStyle(name = "Pixar 3D",          iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenpixar3d/icon.webp",      styleKey = "qwenpixar3d"),
        CartoonStyle(name = "Avatar",            iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/avatar/icon.webp",           styleKey = "avatar"),
        CartoonStyle(name = "Wool",              iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/wool/icon.webp",             styleKey = "wool"),
        CartoonStyle(name = "Keychain",          iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/keychain/icon.webp",         styleKey = "keychain"),
        CartoonStyle(name = "Comic",             iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwencomic/icon.webp",        styleKey = "qwencomic"),
        CartoonStyle(name = "Anime",             iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenanime/icon.webp",        styleKey = "qwenanime"),
        CartoonStyle(name = "Clay",              iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenclay/icon.webp",         styleKey = "qwenclay"),
        CartoonStyle(name = "Jojo",              iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenjojo/icon.webp",         styleKey = "qwenjojo"),
        CartoonStyle(name = "Lego",              iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenlego/icon.webp",         styleKey = "qwenlego"),
        CartoonStyle(name = "Line Art",          iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenlineart/icon.webp",      styleKey = "qwenlineart"),
        CartoonStyle(name = "Macaron",           iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenmacaron/icon.webp",      styleKey = "qwenmacaron"),
        CartoonStyle(name = "Oil Painting",      iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenoilpainting/icon.webp",  styleKey = "qwenoilpainting"),
        CartoonStyle(name = "Origami",           iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenorigami/icon.webp",      styleKey = "qwenorigami"),
        CartoonStyle(name = "Paper Cut",         iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenpaper/icon.webp",        styleKey = "qwenpaper"),
        CartoonStyle(name = "Picasso",           iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenpicasso/icon.webp",      styleKey = "qwenpicasso"),
        CartoonStyle(name = "Pixel Art",         iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenpixel/icon.webp",        styleKey = "qwenpixel"),
        CartoonStyle(name = "Pop Art",           iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenpopart/icon.webp",       styleKey = "qwenpopart"),
        CartoonStyle(name = "Van Gogh",          iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwenvangogh/icon.webp",      styleKey = "qwenvangogh"),
        CartoonStyle(name = "Graffiti",          iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwengraffiti/icon.webp",     styleKey = "qwengraffiti"),
        CartoonStyle(name = "GTA 5 Style",       iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwengta5/icon.webp",         styleKey = "qwengta5"),
        CartoonStyle(name = "Simpsons Style",    iconUrl = "https://res.zeezoo.mobi/genartstore2/filter_styles/qwensimpsons/icon.webp",     styleKey = "qwensimpsons")
    )

    private fun getHairStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Black Hair",   iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp",   styleKey = "black"),
        CartoonStyle(name = "Blonde Hair",  iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/blonde.webp",  styleKey = "blonde"),
        CartoonStyle(name = "Pink Hair",    iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/pink.webp",    styleKey = "pink"),
        CartoonStyle(name = "Purple Hair",  iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/purple.webp",  styleKey = "purple"),
        CartoonStyle(name = "Orange Hair",  iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/orange.webp",  styleKey = "orange"),
        CartoonStyle(name = "Blue Hair",    iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/blue.webp",    styleKey = "blue"),
        CartoonStyle(name = "Green Hair",   iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/green.webp",   styleKey = "green"),
        CartoonStyle(name = "Rainbow Hair", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/rainbow.webp", styleKey = "rainbow"),
        CartoonStyle(name = "Red Hair",     iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/red.webp",     styleKey = "red"),
        CartoonStyle(name = "Silver Hair",  iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/silver.webp",  styleKey = "silver"),
        CartoonStyle(name = "Yellow Hair",  iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/yellow.webp",  styleKey = "yellow"),
        CartoonStyle(name = "Brown Hair",   iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/brown.webp",   styleKey = "brown")
    )

    // ── Birthday ───────────────────────────────────────────────────────────

    private fun getBirthdayManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Birthday Lighting", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/man/Lighting.webp", styleKey = "man_Birthday_Lighting"),
        CartoonStyle(name = "Birthday Black",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/man/Black.webp",    styleKey = "man_Birthday_Black"),
        CartoonStyle(name = "Birthday Golden",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/man/Golden.webp",   styleKey = "man_Birthday_Golden"),
        CartoonStyle(name = "Birthday Purple",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/man/Purple.webp",   styleKey = "man_Birthday_Purple"),
        CartoonStyle(name = "Birthday Red",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/man/Red.webp",      styleKey = "man_Birthday_Red"),
        CartoonStyle(name = "Birthday Disco",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/man/Disco.webp",    styleKey = "man_Birthday_Disco")
    )

    private fun getBirthdayWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Birthday Lighting", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/woman/Lighting.webp", styleKey = "woman_Birthday_Lighting"),
        CartoonStyle(name = "Birthday Black",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/woman/Black.webp",    styleKey = "woman_Birthday_Black"),
        CartoonStyle(name = "Birthday Golden",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/woman/Golden.webp",   styleKey = "woman_Birthday_Golden"),
        CartoonStyle(name = "Birthday Purple",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/woman/Purple.webp",   styleKey = "woman_Birthday_Purple"),
        CartoonStyle(name = "Birthday Red",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/woman/Red.webp",      styleKey = "woman_Birthday_Red"),
        CartoonStyle(name = "Birthday Disco",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Birthday/woman/Disco.webp",    styleKey = "woman_Birthday_Disco")
    )

    // ── Valentine ──────────────────────────────────────────────────────────

    private fun getValentineManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Valentine Nightcity", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Nightcity.webp", styleKey = "man_Valentine_Nightcity"),
        CartoonStyle(name = "Valentine Oldmoney",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Oldmoney.webp",  styleKey = "man_Valentine_Oldmoney"),
        CartoonStyle(name = "Valentine Date",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Date.webp",      styleKey = "man_Valentine_Date"),
        CartoonStyle(name = "Valentine Party",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Party.webp",     styleKey = "man_Valentine_Party"),
        CartoonStyle(name = "Valentine Paris",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Paris.webp",     styleKey = "man_Valentine_Paris"),
        CartoonStyle(name = "Valentine Candle",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/man/Candle.webp",    styleKey = "man_Valentine_Candle")
    )

    private fun getValentineWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Valentine Nightcity", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Nightcity.webp", styleKey = "woman_Valentine_Nightcity"),
        CartoonStyle(name = "Valentine Oldmoney",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Oldmoney.webp",  styleKey = "woman_Valentine_Oldmoney"),
        CartoonStyle(name = "Valentine Date",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Date.webp",      styleKey = "woman_Valentine_Date"),
        CartoonStyle(name = "Valentine Party",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Party.webp",     styleKey = "woman_Valentine_Party"),
        CartoonStyle(name = "Valentine Paris",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Paris.webp",     styleKey = "woman_Valentine_Paris"),
        CartoonStyle(name = "Valentine Candle",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Valentine/woman/Candle.webp",    styleKey = "woman_Valentine_Candle")
    )

    // ── Winter ─────────────────────────────────────────────────────────────

    private fun getWinterManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Winter Lighting",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/Lighting.webp",  styleKey = "man_Winter_Lighting"),
        CartoonStyle(name = "Winter Hooded",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/Hooded.webp",    styleKey = "man_Winter_Hooded"),
        CartoonStyle(name = "Winter Umbrella",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/Umbrella.webp",  styleKey = "man_Winter_Umbrella"),
        CartoonStyle(name = "Winter BlueSpruce",iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/BlueSpruce.webp",styleKey = "man_Winter_BlueSpruce"),
        CartoonStyle(name = "Winter Snowman",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/Snowman.webp",   styleKey = "man_Winter_Snowman"),
        CartoonStyle(name = "Winter Aurora",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/man/Aurora.webp",    styleKey = "man_Winter_Aurora")
    )

    private fun getWinterWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Winter Lighting",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/Lighting.webp",  styleKey = "woman_Winter_Lighting"),
        CartoonStyle(name = "Winter Hooded",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/Hooded.webp",    styleKey = "woman_Winter_Hooded"),
        CartoonStyle(name = "Winter Umbrella",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/Umbrella.webp",  styleKey = "woman_Winter_Umbrella"),
        CartoonStyle(name = "Winter BlueSpruce",iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/BlueSpruce.webp",styleKey = "woman_Winter_BlueSpruce"),
        CartoonStyle(name = "Winter Snowman",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/Snowman.webp",   styleKey = "woman_Winter_Snowman"),
        CartoonStyle(name = "Winter Aurora",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Winter/woman/Aurora.webp",    styleKey = "woman_Winter_Aurora")
    )

    // ── Newyear ────────────────────────────────────────────────────────────

    private fun getNewyearManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Newyear Sofa",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/Sofa.webp",     styleKey = "man_Newyear_Sofa"),
        CartoonStyle(name = "Newyear Disco",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/Disco.webp",    styleKey = "man_Newyear_Disco"),
        CartoonStyle(name = "Newyear Golden",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/Golden.webp",   styleKey = "man_Newyear_Golden"),
        CartoonStyle(name = "Newyear Balloons", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/Balloons.webp", styleKey = "man_Newyear_Balloons"),
        CartoonStyle(name = "Newyear Party",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/Party.webp",    styleKey = "man_Newyear_Party"),
        CartoonStyle(name = "Newyear Yacht",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/man/yacht.webp",    styleKey = "man_Newyear_yacht")
    )

    private fun getNewyearWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Newyear Sofa",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/Sofa.webp",     styleKey = "woman_Newyear_Sofa"),
        CartoonStyle(name = "Newyear Disco",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/Disco.webp",    styleKey = "woman_Newyear_Disco"),
        CartoonStyle(name = "Newyear Golden",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/Golden.webp",   styleKey = "woman_Newyear_Golden"),
        CartoonStyle(name = "Newyear Balloons", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/Balloons.webp", styleKey = "woman_Newyear_Balloons"),
        CartoonStyle(name = "Newyear Party",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/Party.webp",    styleKey = "woman_Newyear_Party"),
        CartoonStyle(name = "Newyear Yacht",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Newyear/woman/yacht.webp",    styleKey = "woman_Newyear_yacht")
    )

    // ── Cute ───────────────────────────────────────────────────────────────

    private fun getCuteManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Cute Hood",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/man/Hood.webp",  styleKey = "man_Cute_Hood"),
        CartoonStyle(name = "Cute Cheek", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/man/Cheek.webp", styleKey = "man_Cute_Cheek"),
        CartoonStyle(name = "Cute Doll",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/man/Doll.webp",  styleKey = "man_Cute_Doll"),
        CartoonStyle(name = "Cute Giant", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/man/Giant.webp", styleKey = "man_Cute_Giant")
    )

    private fun getCuteWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Cute Hood",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/woman/Hood.webp",  styleKey = "woman_Cute_Hood"),
        CartoonStyle(name = "Cute Cheek", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/woman/Cheek.webp", styleKey = "woman_Cute_Cheek"),
        CartoonStyle(name = "Cute Doll",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/woman/Doll.webp",  styleKey = "woman_Cute_Doll"),
        CartoonStyle(name = "Cute Giant", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cute/woman/Giant.webp", styleKey = "woman_Cute_Giant")
    )

    // ── Reward ─────────────────────────────────────────────────────────────

    private fun getRewardManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Reward Bag",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Bag.webp",      styleKey = "man_Reward_Bag"),
        CartoonStyle(name = "Reward Gift",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Gift.webp",     styleKey = "man_Reward_Gift"),
        CartoonStyle(name = "Reward Box",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Box.webp",      styleKey = "man_Reward_Box"),
        CartoonStyle(name = "Reward Ornament", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Ornament.webp", styleKey = "man_Reward_Ornament"),
        CartoonStyle(name = "Reward Globe",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Globe.webp",    styleKey = "man_Reward_Globe"),
        CartoonStyle(name = "Reward Dream",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/man/Dream.webp",    styleKey = "man_Reward_Dream")
    )

    private fun getRewardWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Reward Bag",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Bag.webp",      styleKey = "woman_Reward_Bag"),
        CartoonStyle(name = "Reward Gift",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Gift.webp",     styleKey = "woman_Reward_Gift"),
        CartoonStyle(name = "Reward Box",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Box.webp",      styleKey = "woman_Reward_Box"),
        CartoonStyle(name = "Reward Ornament", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Ornament.webp", styleKey = "woman_Reward_Ornament"),
        CartoonStyle(name = "Reward Globe",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Globe.webp",    styleKey = "woman_Reward_Globe"),
        CartoonStyle(name = "Reward Dream",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Reward/woman/Dream.webp",    styleKey = "woman_Reward_Dream")
    )

    // ── Edgy ───────────────────────────────────────────────────────────────

    private fun getEdgyManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Edgy Caution", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Caution.webp", styleKey = "man_Edgy_Caution"),
        CartoonStyle(name = "Edgy Danger",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Danger.webp",  styleKey = "man_Edgy_Danger"),
        CartoonStyle(name = "Edgy Film",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Film.webp",    styleKey = "man_Edgy_Film"),
        CartoonStyle(name = "Edgy Tapes",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Tapes.webp",   styleKey = "man_Edgy_Tapes"),
        CartoonStyle(name = "Edgy Chain",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Chain.webp",   styleKey = "man_Edgy_Chain"),
        CartoonStyle(name = "Edgy Silk",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/man/Silk.webp",    styleKey = "man_Edgy_Silk")
    )

    private fun getEdgyWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Edgy Caution", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Caution.webp", styleKey = "woman_Edgy_Caution"),
        CartoonStyle(name = "Edgy Danger",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Danger.webp",  styleKey = "woman_Edgy_Danger"),
        CartoonStyle(name = "Edgy Film",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Film.webp",    styleKey = "woman_Edgy_Film"),
        CartoonStyle(name = "Edgy Tapes",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Tapes.webp",   styleKey = "woman_Edgy_Tapes"),
        CartoonStyle(name = "Edgy Chain",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Chain.webp",   styleKey = "woman_Edgy_Chain"),
        CartoonStyle(name = "Edgy Silk",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Edgy/woman/Silk.webp",    styleKey = "woman_Edgy_Silk")
    )

    // ── Modern ─────────────────────────────────────────────────────────────

    private fun getModernManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Modern Cassette", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Cassette.webp", styleKey = "man_Modern_Cassette"),
        CartoonStyle(name = "Modern Chess",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Chess.webp",    styleKey = "man_Modern_Chess"),
        CartoonStyle(name = "Modern Perfume",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Perfume.webp",  styleKey = "man_Modern_Perfume"),
        CartoonStyle(name = "Modern Bag",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Bag.webp",      styleKey = "man_Modern_Bag"),
        CartoonStyle(name = "Modern Camera",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Camera.webp",   styleKey = "man_Modern_Camera"),
        CartoonStyle(name = "Modern Billiard", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/man/Billiard.webp", styleKey = "man_Modern_Billiard")
    )

    private fun getModernWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Modern Cassette", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Cassette.webp", styleKey = "woman_Modern_Cassette"),
        CartoonStyle(name = "Modern Chess",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Chess.webp",    styleKey = "woman_Modern_Chess"),
        CartoonStyle(name = "Modern Perfume",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Perfume.webp",  styleKey = "woman_Modern_Perfume"),
        CartoonStyle(name = "Modern Bag",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Bag.webp",      styleKey = "woman_Modern_Bag"),
        CartoonStyle(name = "Modern Camera",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Camera.webp",   styleKey = "woman_Modern_Camera"),
        CartoonStyle(name = "Modern Billiard", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Modern/woman/Billiard.webp", styleKey = "woman_Modern_Billiard")
    )

    // ── Cinematic ──────────────────────────────────────────────────────────

    private fun getCinematicManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Cinematic Car",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Car.webp",    styleKey = "man_Cinematic_Car"),
        CartoonStyle(name = "Cinematic Train",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Train.webp",  styleKey = "man_Cinematic_Train"),
        CartoonStyle(name = "Cinematic Tram",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Tram.webp",   styleKey = "man_Cinematic_Tram"),
        CartoonStyle(name = "Cinematic Street", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Street.webp", styleKey = "man_Cinematic_Street"),
        CartoonStyle(name = "Cinematic Bus",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Bus.webp",    styleKey = "man_Cinematic_Bus"),
        CartoonStyle(name = "Cinematic Rain",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/man/Rain.webp",   styleKey = "man_Cinematic_Rain")
    )

    private fun getCinematicWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Cinematic Car",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Car.webp",    styleKey = "woman_Cinematic_Car"),
        CartoonStyle(name = "Cinematic Train",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Train.webp",  styleKey = "woman_Cinematic_Train"),
        CartoonStyle(name = "Cinematic Tram",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Tram.webp",   styleKey = "woman_Cinematic_Tram"),
        CartoonStyle(name = "Cinematic Street", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Street.webp", styleKey = "woman_Cinematic_Street"),
        CartoonStyle(name = "Cinematic Bus",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Bus.webp",    styleKey = "woman_Cinematic_Bus"),
        CartoonStyle(name = "Cinematic Rain",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cinematic/woman/Rain.webp",   styleKey = "woman_Cinematic_Rain")
    )

    // ── Spotlight ──────────────────────────────────────────────────────────

    private fun getSpotlightManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Spotlight Black",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Black.webp",  styleKey = "man_Spotlight_Black"),
        CartoonStyle(name = "Spotlight Blue",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Blue.webp",   styleKey = "man_Spotlight_Blue"),
        CartoonStyle(name = "Spotlight Red",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Red.webp",    styleKey = "man_Spotlight_Red"),
        CartoonStyle(name = "Spotlight Gray",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Gray.webp",   styleKey = "man_Spotlight_Gray"),
        CartoonStyle(name = "Spotlight Neon",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Neon.webp",   styleKey = "man_Spotlight_Neon"),
        CartoonStyle(name = "Spotlight Streak", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/man/Streak.webp", styleKey = "man_Spotlight_Streak")
    )

    private fun getSpotlightWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Spotlight Black",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Black.webp",  styleKey = "woman_Spotlight_Black"),
        CartoonStyle(name = "Spotlight Blue",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Blue.webp",   styleKey = "woman_Spotlight_Blue"),
        CartoonStyle(name = "Spotlight Red",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Red.webp",    styleKey = "woman_Spotlight_Red"),
        CartoonStyle(name = "Spotlight Gray",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Gray.webp",   styleKey = "woman_Spotlight_Gray"),
        CartoonStyle(name = "Spotlight Neon",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Neon.webp",   styleKey = "woman_Spotlight_Neon"),
        CartoonStyle(name = "Spotlight Streak", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Spotlight/woman/Streak.webp", styleKey = "woman_Spotlight_Streak")
    )

    // ── Golden ─────────────────────────────────────────────────────────────

    private fun getGoldenManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Golden Brown", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Brown.webp", styleKey = "man_Golden_Brown"),
        CartoonStyle(name = "Golden Blue",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Blue.webp",  styleKey = "man_Golden_Blue"),
        CartoonStyle(name = "Golden Gray",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Gray.webp",  styleKey = "man_Golden_Gray"),
        CartoonStyle(name = "Golden Green", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Green.webp", styleKey = "man_Golden_Green"),
        CartoonStyle(name = "Golden Pink",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Pink.webp",  styleKey = "man_Golden_Pink"),
        CartoonStyle(name = "Golden Light", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/man/Light.webp", styleKey = "man_Golden_Light")
    )

    private fun getGoldenWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Golden Brown", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Brown.webp", styleKey = "woman_Golden_Brown"),
        CartoonStyle(name = "Golden Blue",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Blue.webp",  styleKey = "woman_Golden_Blue"),
        CartoonStyle(name = "Golden Gray",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Gray.webp",  styleKey = "woman_Golden_Gray"),
        CartoonStyle(name = "Golden Green", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Green.webp", styleKey = "woman_Golden_Green"),
        CartoonStyle(name = "Golden Pink",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Pink.webp",  styleKey = "woman_Golden_Pink"),
        CartoonStyle(name = "Golden Light", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Golden/woman/Light.webp", styleKey = "woman_Golden_Light")
    )

    // ── Circle ─────────────────────────────────────────────────────────────

    private fun getCircleManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Circle Red",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Red.webp",    styleKey = "man_Circle_Red"),
        CartoonStyle(name = "Circle Orange", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Orange.webp", styleKey = "man_Circle_Orange"),
        CartoonStyle(name = "Circle Blue",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Blue.webp",   styleKey = "man_Circle_Blue"),
        CartoonStyle(name = "Circle Black",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Black.webp",  styleKey = "man_Circle_Black"),
        CartoonStyle(name = "Circle Yellow", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Yellow.webp", styleKey = "man_Circle_Yellow"),
        CartoonStyle(name = "Circle Green",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/man/Green.webp",  styleKey = "man_Circle_Green")
    )

    private fun getCircleWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Circle Red",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Red.webp",    styleKey = "woman_Circle_Red"),
        CartoonStyle(name = "Circle Orange", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Orange.webp", styleKey = "woman_Circle_Orange"),
        CartoonStyle(name = "Circle Blue",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Blue.webp",   styleKey = "woman_Circle_Blue"),
        CartoonStyle(name = "Circle Black",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Black.webp",  styleKey = "woman_Circle_Black"),
        CartoonStyle(name = "Circle Yellow", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Yellow.webp", styleKey = "woman_Circle_Yellow"),
        CartoonStyle(name = "Circle Green",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Circle/woman/Green.webp",  styleKey = "woman_Circle_Green")
    )

    // ── Cube ───────────────────────────────────────────────────────────────

    private fun getCubeManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Cube Dice",       iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Dice.webp",       styleKey = "man_Cube_Dice"),
        CartoonStyle(name = "Cube Brick",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Brick.webp",      styleKey = "man_Cube_Brick"),
        CartoonStyle(name = "Cube Ice",        iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Ice.webp",        styleKey = "man_Cube_Ice"),
        CartoonStyle(name = "Cube Rubik",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Rubik.webp",      styleKey = "man_Cube_Rubik"),
        CartoonStyle(name = "Cube Television", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Television.webp", styleKey = "man_Cube_Television"),
        CartoonStyle(name = "Cube Insta",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/man/Insta.webp",      styleKey = "man_Cube_Insta")
    )

    private fun getCubeWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Cube Dice",       iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Dice.webp",       styleKey = "woman_Cube_Dice"),
        CartoonStyle(name = "Cube Brick",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Brick.webp",      styleKey = "woman_Cube_Brick"),
        CartoonStyle(name = "Cube Ice",        iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Ice.webp",        styleKey = "woman_Cube_Ice"),
        CartoonStyle(name = "Cube Rubik",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Rubik.webp",      styleKey = "woman_Cube_Rubik"),
        CartoonStyle(name = "Cube Television", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Television.webp", styleKey = "woman_Cube_Television"),
        CartoonStyle(name = "Cube Insta",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Cube/woman/Insta.webp",      styleKey = "woman_Cube_Insta")
    )

    // ── Studio ─────────────────────────────────────────────────────────────

    private fun getStudioManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Studio Jacket", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/Jacket.webp", styleKey = "man_Studio_Jacket"),
        CartoonStyle(name = "Studio Black",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/Black.webp",  styleKey = "man_Studio_Black"),
        CartoonStyle(name = "Studio Cuboid", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/Cuboid.webp", styleKey = "man_Studio_Cuboid"),
        CartoonStyle(name = "Studio White",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/White.webp",  styleKey = "man_Studio_White"),
        CartoonStyle(name = "Studio Table",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/Table.webp",  styleKey = "man_Studio_Table"),
        CartoonStyle(name = "Studio Chair",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/man/Chair.webp",  styleKey = "man_Studio_Chair")
    )

    private fun getStudioWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Studio Jacket", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/Jacket.webp", styleKey = "woman_Studio_Jacket"),
        CartoonStyle(name = "Studio Black",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/Black.webp",  styleKey = "woman_Studio_Black"),
        CartoonStyle(name = "Studio Cuboid", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/Cuboid.webp", styleKey = "woman_Studio_Cuboid"),
        CartoonStyle(name = "Studio White",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/White.webp",  styleKey = "woman_Studio_White"),
        CartoonStyle(name = "Studio Table",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/Table.webp",  styleKey = "woman_Studio_Table"),
        CartoonStyle(name = "Studio Chair",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Studio/woman/Chair.webp",  styleKey = "woman_Studio_Chair")
    )

    // ── Bwstudio ───────────────────────────────────────────────────────────

    private fun getBwstudioManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Bwstudio Studio",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Studio.webp",   styleKey = "man_Bwstudio_Studio"),
        CartoonStyle(name = "Bwstudio Fence",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Fence.webp",    styleKey = "man_Bwstudio_Fence"),
        CartoonStyle(name = "Bwstudio Car",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Car.webp",      styleKey = "man_Bwstudio_Car"),
        CartoonStyle(name = "Bwstudio Umbrella", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Umbrella.webp", styleKey = "man_Bwstudio_Umbrella"),
        CartoonStyle(name = "Bwstudio Field",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Field.webp",    styleKey = "man_Bwstudio_Field"),
        CartoonStyle(name = "Bwstudio Wall",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/man/Wall.webp",     styleKey = "man_Bwstudio_Wall")
    )

    private fun getBwstudioWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Bwstudio Studio",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Studio.webp",   styleKey = "woman_Bwstudio_Studio"),
        CartoonStyle(name = "Bwstudio Fence",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Fence.webp",    styleKey = "woman_Bwstudio_Fence"),
        CartoonStyle(name = "Bwstudio Car",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Car.webp",      styleKey = "woman_Bwstudio_Car"),
        CartoonStyle(name = "Bwstudio Umbrella", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Umbrella.webp", styleKey = "woman_Bwstudio_Umbrella"),
        CartoonStyle(name = "Bwstudio Field",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Field.webp",    styleKey = "woman_Bwstudio_Field"),
        CartoonStyle(name = "Bwstudio Wall",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Bwstudio/woman/Wall.webp",     styleKey = "woman_Bwstudio_Wall")
    )

    // ── Monochromatic ──────────────────────────────────────────────────────

    private fun getMonochromaticManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Monochromatic Orange", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/Orange.webp", styleKey = "man_Monochromatic_Orange"),
        CartoonStyle(name = "Monochromatic Gold",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/Gold.webp",   styleKey = "man_Monochromatic_Gold"),
        CartoonStyle(name = "Monochromatic Purple", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/Purple.webp", styleKey = "man_Monochromatic_Purple"),
        CartoonStyle(name = "Monochromatic Red",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/Red.webp",    styleKey = "man_Monochromatic_Red"),
        CartoonStyle(name = "Monochromatic Blue",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/Blue.webp",   styleKey = "man_Monochromatic_Blue"),
        CartoonStyle(name = "Monochromatic White",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/man/White.webp",  styleKey = "man_Monochromatic_White")
    )

    private fun getMonochromaticWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Monochromatic Orange", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/Orange.webp", styleKey = "woman_Monochromatic_Orange"),
        CartoonStyle(name = "Monochromatic Gold",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/Gold.webp",   styleKey = "woman_Monochromatic_Gold"),
        CartoonStyle(name = "Monochromatic Purple", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/Purple.webp", styleKey = "woman_Monochromatic_Purple"),
        CartoonStyle(name = "Monochromatic Red",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/Red.webp",    styleKey = "woman_Monochromatic_Red"),
        CartoonStyle(name = "Monochromatic Blue",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/Blue.webp",   styleKey = "woman_Monochromatic_Blue"),
        CartoonStyle(name = "Monochromatic White",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Monochromatic/woman/White.webp",  styleKey = "woman_Monochromatic_White")
    )

    // ── LinkedIn ───────────────────────────────────────────────────────────

    private fun getLinkedInManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "LinkedIn Pistachio", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/Pistachio.webp", styleKey = "man_LinkedIn_Pistachio"),
        CartoonStyle(name = "LinkedIn Black",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/Black.webp",     styleKey = "man_LinkedIn_Black"),
        CartoonStyle(name = "LinkedIn White",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/White.webp",     styleKey = "man_LinkedIn_White"),
        CartoonStyle(name = "LinkedIn Navy",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/Navy.webp",      styleKey = "man_LinkedIn_Navy"),
        CartoonStyle(name = "LinkedIn Beige",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/Beige.webp",     styleKey = "man_LinkedIn_Beige"),
        CartoonStyle(name = "LinkedIn Grey",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/man/Grey.webp",      styleKey = "man_LinkedIn_Grey")
    )

    private fun getLinkedInWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "LinkedIn Pistachio", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/Pistachio.webp", styleKey = "woman_LinkedIn_Pistachio"),
        CartoonStyle(name = "LinkedIn Black",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/Black.webp",     styleKey = "woman_LinkedIn_Black"),
        CartoonStyle(name = "LinkedIn White",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/White.webp",     styleKey = "woman_LinkedIn_White"),
        CartoonStyle(name = "LinkedIn Navy",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/Navy.webp",      styleKey = "woman_LinkedIn_Navy"),
        CartoonStyle(name = "LinkedIn Beige",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/Beige.webp",     styleKey = "woman_LinkedIn_Beige"),
        CartoonStyle(name = "LinkedIn Grey",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/LinkedIn/woman/Grey.webp",      styleKey = "woman_LinkedIn_Grey")
    )

    // ── Suit ───────────────────────────────────────────────────────────────

    private fun getSuitManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Suit Pistachio", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Pistachio.webp", styleKey = "man_Suit_Pistachio"),
        CartoonStyle(name = "Suit Black",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Black.webp",     styleKey = "man_Suit_Black"),
        CartoonStyle(name = "Suit Beige",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Beige.webp",     styleKey = "man_Suit_Beige"),
        CartoonStyle(name = "Suit Navy",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Navy.webp",      styleKey = "man_Suit_Navy"),
        CartoonStyle(name = "Suit Grey",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Grey.webp",      styleKey = "man_Suit_Grey"),
        CartoonStyle(name = "Suit Brown",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/man/Brown.webp",     styleKey = "man_Suit_Brown")
    )

    private fun getSuitWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Suit Pistachio", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Pistachio.webp", styleKey = "woman_Suit_Pistachio"),
        CartoonStyle(name = "Suit Black",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Black.webp",     styleKey = "woman_Suit_Black"),
        CartoonStyle(name = "Suit Beige",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Beige.webp",     styleKey = "woman_Suit_Beige"),
        CartoonStyle(name = "Suit Navy",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Navy.webp",      styleKey = "woman_Suit_Navy"),
        CartoonStyle(name = "Suit Grey",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Grey.webp",      styleKey = "woman_Suit_Grey"),
        CartoonStyle(name = "Suit Brown",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Suit/woman/Brown.webp",     styleKey = "woman_Suit_Brown")
    )

    // ── Christmas ──────────────────────────────────────────────────────────

    private fun getChristmasManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Christmas Red",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/Red.webp",      styleKey = "man_Christmas_Red"),
        CartoonStyle(name = "Christmas White",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/White.webp",    styleKey = "man_Christmas_White"),
        CartoonStyle(name = "Christmas Green",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/Green.webp",    styleKey = "man_Christmas_Green"),
        CartoonStyle(name = "Christmas Gold",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/Gold.webp",     styleKey = "man_Christmas_Gold"),
        CartoonStyle(name = "Christmas Burgundy", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/Burgundy.webp", styleKey = "man_Christmas_Burgundy"),
        CartoonStyle(name = "Christmas Silver",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/man/Silver.webp",   styleKey = "man_Christmas_Silver")
    )

    private fun getChristmasWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Christmas Red",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/Red.webp",      styleKey = "woman_Christmas_Red"),
        CartoonStyle(name = "Christmas White",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/White.webp",    styleKey = "woman_Christmas_White"),
        CartoonStyle(name = "Christmas Green",    iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/Green.webp",    styleKey = "woman_Christmas_Green"),
        CartoonStyle(name = "Christmas Gold",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/Gold.webp",     styleKey = "woman_Christmas_Gold"),
        CartoonStyle(name = "Christmas Burgundy", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/Burgundy.webp", styleKey = "woman_Christmas_Burgundy"),
        CartoonStyle(name = "Christmas Silver",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Christmas/woman/Silver.webp",   styleKey = "woman_Christmas_Silver")
    )

    // ── BabyChristmas ──────────────────────────────────────────────────────

    private fun getBabyChristmasManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "BabyChristmas Cozy",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Cozy.webp",   styleKey = "man_BabyChristmas_Cozy"),
        CartoonStyle(name = "BabyChristmas Winter", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Winter.webp", styleKey = "man_BabyChristmas_Winter"),
        CartoonStyle(name = "BabyChristmas Tiny",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Tiny.webp",   styleKey = "man_BabyChristmas_Tiny"),
        CartoonStyle(name = "BabyChristmas Sweet",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Sweet.webp",  styleKey = "man_BabyChristmas_Sweet"),
        CartoonStyle(name = "BabyChristmas Dream",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Dream.webp",  styleKey = "man_BabyChristmas_Dream"),
        CartoonStyle(name = "BabyChristmas Soft",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/man/Soft.webp",   styleKey = "man_BabyChristmas_Soft")
    )

    private fun getBabyChristmasWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "BabyChristmas Cozy",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Cozy.webp",   styleKey = "woman_BabyChristmas_Cozy"),
        CartoonStyle(name = "BabyChristmas Winter", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Winter.webp", styleKey = "woman_BabyChristmas_Winter"),
        CartoonStyle(name = "BabyChristmas Tiny",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Tiny.webp",   styleKey = "woman_BabyChristmas_Tiny"),
        CartoonStyle(name = "BabyChristmas Sweet",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Sweet.webp",  styleKey = "woman_BabyChristmas_Sweet"),
        CartoonStyle(name = "BabyChristmas Dream",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Dream.webp",  styleKey = "woman_BabyChristmas_Dream"),
        CartoonStyle(name = "BabyChristmas Soft",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/BabyChristmas/woman/Soft.webp",   styleKey = "woman_BabyChristmas_Soft")
    )

    // ── Lunar ──────────────────────────────────────────────────────────────

    private fun getLunarManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Lunar Lion",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Lion.webp",      styleKey = "man_Lunar_Lion"),
        CartoonStyle(name = "Lunar Greeting",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Greeting.webp",  styleKey = "man_Lunar_Greeting"),
        CartoonStyle(name = "Lunar Envelopes", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Envelopes.webp", styleKey = "man_Lunar_Envelopes"),
        CartoonStyle(name = "Lunar Heritage",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Heritage.webp",  styleKey = "man_Lunar_Heritage"),
        CartoonStyle(name = "Lunar Porcelain", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Porcelain.webp", styleKey = "man_Lunar_Porcelain"),
        CartoonStyle(name = "Lunar Fortune",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/man/Fortune.webp",   styleKey = "man_Lunar_Fortune")
    )

    private fun getLunarWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Lunar Lion",      iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Lion.webp",      styleKey = "woman_Lunar_Lion"),
        CartoonStyle(name = "Lunar Greeting",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Greeting.webp",  styleKey = "woman_Lunar_Greeting"),
        CartoonStyle(name = "Lunar Envelopes", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Envelopes.webp", styleKey = "woman_Lunar_Envelopes"),
        CartoonStyle(name = "Lunar Heritage",  iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Heritage.webp",  styleKey = "woman_Lunar_Heritage"),
        CartoonStyle(name = "Lunar Porcelain", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Porcelain.webp", styleKey = "woman_Lunar_Porcelain"),
        CartoonStyle(name = "Lunar Fortune",   iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/Lunar/woman/Fortune.webp",   styleKey = "woman_Lunar_Fortune")
    )

    // ── VNLuna ─────────────────────────────────────────────────────────────

    private fun getVNLunaManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "VNLuna Banhchung", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Banhchung.webp", styleKey = "man_VNLuna_Banhchung"),
        CartoonStyle(name = "VNLuna Mai",       iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Mai.webp",       styleKey = "man_VNLuna_Mai"),
        CartoonStyle(name = "VNLuna Dao",       iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Dao.webp",       styleKey = "man_VNLuna_Dao"),
        CartoonStyle(name = "VNLuna Candy",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Candy.webp",     styleKey = "man_VNLuna_Candy"),
        CartoonStyle(name = "VNLuna Lucky",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Lucky.webp",     styleKey = "man_VNLuna_Lucky"),
        CartoonStyle(name = "VNLuna Horse",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/man/Horse.webp",     styleKey = "man_VNLuna_Horse")
    )

    private fun getVNLunaWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "VNLuna Banhchung", iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Banhchung.webp", styleKey = "woman_VNLuna_Banhchung"),
        CartoonStyle(name = "VNLuna Mai",       iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Mai.webp",       styleKey = "woman_VNLuna_Mai"),
        CartoonStyle(name = "VNLuna Dao",       iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Dao.webp",       styleKey = "woman_VNLuna_Dao"),
        CartoonStyle(name = "VNLuna Candy",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Candy.webp",     styleKey = "woman_VNLuna_Candy"),
        CartoonStyle(name = "VNLuna Lucky",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Lucky.webp",     styleKey = "woman_VNLuna_Lucky"),
        CartoonStyle(name = "VNLuna Horse",     iconUrl = "https://res.zeezoo.mobi/aiphoto/headshotstyles/VNLuna/woman/Horse.webp",     styleKey = "woman_VNLuna_Horse")
    )

    // ── Aging ──────────────────────────────────────────────────────────────

    private fun getAgingManStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Aging 20", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "man_aging20"),
        CartoonStyle(name = "Aging 30", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "man_aging30"),
        CartoonStyle(name = "Aging 40", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "man_aging40"),
        CartoonStyle(name = "Aging 50", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "man_aging50"),
        CartoonStyle(name = "Aging 60", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "man_aging60"),
        CartoonStyle(name = "Aging 70", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "man_aging70"),
        CartoonStyle(name = "Aging 80", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "man_aging80")
    )

    private fun getAgingWomanStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Aging 20", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "woman_aging20"),
        CartoonStyle(name = "Aging 30", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "woman_aging30"),
        CartoonStyle(name = "Aging 40", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "woman_aging40"),
        CartoonStyle(name = "Aging 50", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "woman_aging50"),
        CartoonStyle(name = "Aging 60", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "woman_aging60"),
        CartoonStyle(name = "Aging 70", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "woman_aging70"),
        CartoonStyle(name = "Aging 80", iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "woman_aging80")
    )
    // ── Haircut Styles ─────────────────────────────────────────────────────

    private fun getHaircutStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Bod",                 iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_bod"),
        CartoonStyle(name = "Curly",               iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_curly"),
        CartoonStyle(name = "Straight",            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_straight"),
        CartoonStyle(name = "Wavy",                iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_wavy"),
        CartoonStyle(name = "Panytail",            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_panytail"),
        CartoonStyle(name = "Short Box",           iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_shortbox"),
        CartoonStyle(name = "Curly Pixie",         iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_curlypixie"),
        CartoonStyle(name = "Wispy Layer",         iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_wispy_layer"),
        CartoonStyle(name = "Low Bun",             iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_lowbun"),
        CartoonStyle(name = "French Braid",        iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_frenchbraid"),
        CartoonStyle(name = "Three Strand",        iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "haircut_threestrand")
    )

// ── Figurine & Art Styles ───────────────────────────────────────────────

    private fun getFigurineStyles(): List<CartoonStyle> = listOf(
        CartoonStyle(name = "Figurine 02",         iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "figurine02"),
        CartoonStyle(name = "Ghibli",              iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "ghibli_runninghub_1946470668230619137"),
        CartoonStyle(name = "3D Blind Box",        iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "3dblindbox"),
        CartoonStyle(name = "3D Chibi Cute",       iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "3dchibicute"),
        CartoonStyle(name = "PVC Figure",          iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "pvc_figure"),
        CartoonStyle(name = "GTA 5",               iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "gta5"),
        CartoonStyle(name = "Retro Old Movies",    iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "retro_style_old_movies"),
        CartoonStyle(name = "Pop Art",             iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "pop_art"),
        CartoonStyle(name = "Comic Lineart",       iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "comic_lineart"),
        CartoonStyle(name = "Pixel 2",             iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "pixel2"),
        CartoonStyle(name = "Pixel 1",             iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "pixel1"),
        CartoonStyle(name = "Animesh Flower",      iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "animeshflower"),
        CartoonStyle(name = "Simpsons",            iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "simpsons"),
        CartoonStyle(name = "Chibi Sunflower",     iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "chibisunflower"),
        CartoonStyle(name = "Animesh Flat",        iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "animesh_flat"),
        CartoonStyle(name = "GTA 5 Artwork",       iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "gta5_artwork"),
        CartoonStyle(name = "Toonify",             iconUrl = "https://res.zeezoo.mobi/aiphoto/hairstyles/black.webp", styleKey = "toonify")
    )
}