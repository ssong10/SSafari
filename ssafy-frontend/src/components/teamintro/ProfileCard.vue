<template>
  <div class="profile-body">
    <!-- 데스크탑, 태블릿 사이즈일 때 -->
    <div class="profile-box">
      <div class="profile-img-box">
        <img :src="imgSrc">
      </div>
      <div class="profile-content">
        <h3>{{ name }}</h3>
        <div class="profile-content-detail">
          <div class="my-3 black--text">{{ position }}</div>
          <v-divider></v-divider>
          <div class="profile-intro">{{ intro }}</div>
          <div class="profile-skills-title">Main Skills</div>
          <div class="profile-skill-tag">
            <v-chip v-for="i in this.skills.length" :key="i" :color="fontColor[skills[i - 1]]">{{ skills[i - 1] }}</v-chip>
          </div>
          <div class="sns-button">
            <a class="btn" :href="`${gitUrl}`" target="_blank">
              <i class="fab fa-github"></i>
            </a>
            <a class="btn" :href="`mailto:${email}`">
              <i class="fas fa-envelope-square"></i>
            </a>
          </div>
        </div>
      </div>
    </div>

    <!-- 모바일 사이즈일 때 hover 효과 적용이 어려우므로 다른 레이아웃 구성 -->
    <div class="mobile-profile-box">
      <v-card
        class="mx-auto"
        max-width="300"
      >
        <v-img
          class="white--text align-end"
          height="400px"
          :src="imgSrc"
        >
          <v-card-title><strong>{{ name }}</strong></v-card-title>
        </v-img>

        <v-card-subtitle class="pb-2">{{ position }}</v-card-subtitle>

        <v-card-text class="text--primary">
          <div>{{ intro }}</div>
        </v-card-text>
        <v-card-text class="text--primary main-skills-mobile">
          <div class="main-skills-title" style="font-size: 19px; font-weight: 550; padding-bottom: 7px;">Main Skills</div>
          <div class="skill-tags-mobile">
            <v-chip v-for="i in this.skills.length" :key="i" :color="fontColor[skills[i - 1]]">{{ skills[i - 1] }}</v-chip>
          </div>
        </v-card-text>

        <v-card-actions>
          <v-btn color="orange" text>
            <a class="mob-btn" :href="`${gitUrl}`" target="_blank">Github</a>
          </v-btn>

          <v-btn color="orange" text>
            <a class="mob-btn" :href="`mailto:${email}`">E-mail</a>
          </v-btn>
        </v-card-actions>
      </v-card>
    </div>
  </div> 
</template>

<script>
import '@/assets/css/ProfileCard.css'
export default {
	name: 'ProfileCard',
	data() {
		return {
			fontColor: {
				'MySQL': '#6D77D9',
				'Spring': '#A5DC71',
				'HTML': '#F48222',
				'AWS': '#F19D3F',
				'Vue.js': '#4FCD75',
				'CSS': '#6E88F1',
				'UI/UX': '#F16EB7'
			}
		}
	},
	props: {
		imgSrc: { type: String },
		name: { type: String },
		position: { type: String },
		intro: { type: String },
		skills: { type: Array },
    gitUrl: { type: String },
    email: { type: String }
	}
}
</script>