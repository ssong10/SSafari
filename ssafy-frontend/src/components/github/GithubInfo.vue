<template>
	<div class="github">
		<div v-if="githubid === ''">
			<p class="text-center" style="font-size: 150px;"><i class="fab fa-github-alt"></i></p>
			<p class="text-center pb-6" style="font-size: 28px; font-family: 'Noto Sans KR', sans-serif; font-weight: 600;">Github ID를 등록하지 않았습니다.</p>
			<p class="text-center" style="font-size: 15px; font-family: 'Noto Sans KR', sans-serif;">좌측 '회원정보 수정'에서 Github ID를 등록해주세요.</p>
			</div>

		<div v-else>
			<!-- github header -->
			<nav class="github-nav">
				<ul>
					<li @click="showGithubSection(0)"><span class="on" id="github-info-menu">github info</span></li>
					<li @click="showGithubSection(1)"><span class="" id="github-repos-menu">repository info</span></li>
				</ul>
			</nav>
			
			<!-- github info 클릭했을 때 기본정보 -->
			<div v-if="selectStatus === 0" class="github-info-basic">
				<div class="github-info-wrapper ma-10">
					<p class="github-profile-img">
						<img :src="profileImgUrl" alt="github-profile=image" id="img-1">
					</p>
					<p class="github-profile-img2" style="text-align: center;">
						<img :src="profileImgUrl" alt="github-profile=image" id="img-2" style="margin-left: auto; margin-right: auto; display: block;">
					</p>
					<div class="github-name">
						<p v-if="githubInformation.name === null">No Register Name</p>
						<p v-else>{{ githubInformation.name }}</p>
						<p @click="goGithub(githubid)" class="d-inline-block">(<span>{{ githubid }}</span>)</p>
					</div>
					<div class="github-info-summary">
						<div class="github-info-summary-company">
							<p><i class="far fa-building"></i>{{ githubInformation.company }}</p>
							<p><i class="fas fa-blog"></i><span @click="goBlog(githubInformation.blog)">{{ githubInformation.blog }}</span></p>
							<p><i class="fas fa-location-arrow"></i>{{ githubInformation.location }}</p>
						</div>
						<div class="github-info-repos-summary">
							<p><i class="fas fa-dice-one"></i>Repository Count : {{ githubInformation.publicReposCount }}</p>
							<p><i class="fas fa-dice-two"></i>Followers : {{ githubInformation.followers }}</p>
							<p><i class="fas fa-dice-three"></i>Following : {{ githubInformation.following }}</p>
						</div>
					</div>
				</div>
			</div>

			<!-- repository info 클릭했을 때 repository 정보 -->
			<div v-else class="github-info-repository">
				<div class="github-info-repository-wrapper ma-10">
					<div class="github-info-repository-title">{{ githubid }}'s Repository</div>
					<v-divider color="#424242"></v-divider>
					<div class="github-repository-wrapper">
						<div v-if="githubReposInfo.length === 0" class="no-repos-alert">No Repository</div>
						<div v-else class="github-repository" v-for="i in githubReposInfo.length" :key="i">
							<div class="github-repos-header">
								<div @click="goRepos(githubReposInfo[i - 1].name)">{{ githubReposInfo[i - 1].name }}</div>
								<div><i class="fas fa-star"></i>{{ githubReposInfo[i - 1].starCount }}</div>
							</div>
							<div class="github-repos-contents">
								<div class="github-repos-contents-1">
									<div class="github-repos-contents-description">
										<div class="description-title">Description</div>
										<div>{{ githubReposInfo[i - 1].description}}</div>
									</div>
									<div class="github-repos-contents-clone">
										<div class="clone-title">Clone with HTTPS<span class="clone-button" @click="copyCloneUrl(githubReposInfo[i - 1].cloneUrl)"><i class="fas fa-copy"></i><span class="pl-1" style="font-size: 15px;">(copy)</span></span></div>
										<div class="clone-url">{{ githubReposInfo[i - 1].cloneUrl }}</div>
									</div>
								</div>
								<div class="github-repos-contents-2">
									<div v-if="githubReposInfo[i - 1].reposChart.legend.data.length === 0" class="no-chart-alert">No Language!</div>
									<v-chart v-else :options="githubReposInfo[i - 1].reposChart"/>
								</div>
							</div>
						<v-divider></v-divider>
						</div>
					</div>
				</div>
			</div>
			<v-snackbar v-model="snackbar" :timeout="2500" :bottom="true">
				<span class="mx-3">{{ copyText }}</span>
				<v-btn color="#f7b157" text @click="snackbar = false">Close</v-btn>
			</v-snackbar>
		</div>
	</div>
</template>

<script>
import axios from 'axios'
import ECharts from 'vue-echarts'
import 'echarts/lib/chart/bar'
import 'echarts/lib/component/legend'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/axisPointer'
import '@/assets/css/GithubInfo.css'

export default {
	name: 'GithubInfo',
	props: {
		githubid: {type: String}
	},
	data() {
		return {
			profileImgUrl: '',
			githubInformation: null,
			selectStatus: 0,
			githubReposInfo: null,
			snackbar: false,
			copyText: 'Copy Clone URL!'
    }
	},
	components: {
		'v-chart': ECharts
	},
	mounted() {
		axios.get(`https://api.github.com/users/${this.githubid}`)
			.then(response => {
				const githubData = response.data
				this.profileImgUrl = githubData['avatar_url']
				this.githubInformation = {
					githubID: this.githubid,
					name: githubData.name,
					company: githubData.company === null ? 'No company' : githubData.company,
					blog: githubData.blog === '' ? 'No blog' : githubData.blog,
					location: githubData.location === null ? 'No location' : githubData.location,
					publicReposCount: githubData['public_repos'],
					followers: githubData.followers,
					following: githubData.following
				}
			})
			.catch(error => {
				console.log(error)
			})
		let reposArray = []
		axios.get(`https://api.github.com/users/${this.githubid}/repos`)
			.then(response => {
				const githubReposData = response.data
				githubReposData.forEach(function(data) {
					let repoInfo = {
						'name': data.name,
						'starCount': data['stargazers_count'],
						'reposUrl': data['html_url'],
						'cloneUrl': data['clone_url'],
						'description': data['description'],
						'owner': data.owner.login
					}
					axios.get(`https://api.github.com/repos/${repoInfo['owner']}/${repoInfo['name']}/languages`)
						.then(res => {
							let colorSet = ['#ff8008', '#f7b157', '#ffc837']
							let chartFormat = {
								color: [],
								grid: {
									left: '15%',
									right: '15%',
									tooltip: {
										position: 'right'
									}
								},
								tooltip: {
									trigger: 'item',
									axisPointer: {
										type: 'shadow'
									}
								},
								backgroundColor: '#fff',
								legend: {
									bottom: 5,
									data: []
								},
								dataset: {
									source: [
										['Language'],
										['']
									]
								},
								xAxis: {
									type: 'category'
								},
								yAxis: {
									type: 'value',
									name: 'Using Rate[%]',
									max: 100,
									maxInterval: 20
								},
								series: []
							}
							let langs = res.data
							if (Object.keys(langs).length !== 0) {
								function langSum(obj) {
									return Object.keys(obj).reduce((sum, key) => sum + obj[key], 0)
								}
								let langSumValue = langSum(langs)
								let flagIdx = 0
								for (let lang in langs) {
									if (flagIdx === 3 || flagIdx === Object.keys(langs).length) { break }
									chartFormat.legend.data.push(lang)
									chartFormat.dataset.source[0].push(lang)
									chartFormat.dataset.source[1].push(`${((langs[lang] / langSumValue) * 100).toFixed(2)}`)
									chartFormat.color.push(colorSet[flagIdx])
									chartFormat.series.push({type: 'bar'})
									flagIdx += 1
								}
							}
							repoInfo['reposChart'] = chartFormat
						})
					reposArray.push(repoInfo)
				})
			})
			.catch(error => console.log(error))
		this.githubReposInfo = reposArray
		window.addEventListener('resize', function() {
			const chartTags = document.querySelectorAll('canvas')
			chartTags.forEach(chartTag => {
				if (window.innerWidth <= 690) {
					chartTag.style.width = '390px'
					chartTag.style.height = '260px'
				} else {
					chartTag.style.width = '600px'
					chartTag.style.height = '400px'
				}
			})
		})
	},
	methods: {
		showGithubSection(selectMenu) {
			const githubInfoMenuTab = document.querySelector('#github-info-menu')
			const githubReposMenuTab = document.querySelector('#github-repos-menu')
			if (selectMenu === 0) {
				this.selectStatus = selectMenu
				githubInfoMenuTab.setAttribute('class', 'on')
				githubReposMenuTab.setAttribute('class', '')
			} else {
				this.selectStatus = selectMenu
				githubInfoMenuTab.setAttribute('class', '')
				githubReposMenuTab.setAttribute('class', 'on')
			}
		},
		goGithub(idVal) {
			window.open(`https://github.com/${idVal}`)
		},
		goBlog(url) {
			if (url !== 'No blog') {
				window.open(`https://${url}`)
			} else {
				alert('github에 Blog 정보를 입력하지 않았습니다.')
			}
		},
		goRepos(url) {
			window.open(`https://github.com/${this.githubid}/${url}`)
		},
		copyCloneUrl(url) {
			this.snackbar = true
			const copyBox = document.createElement('textarea')
			copyBox.value = url
			document.body.appendChild(copyBox)
			copyBox.select()
			document.execCommand('copy')
			document.body.removeChild(copyBox)
		},
		async getGithubInfo(userName) {
			if(response.status !== 200) { return }
		},
	}
}
</script>