import Vue from 'vue'
import Vuex from 'vuex'
import VueSession from 'vue-session'
import jwtDecode from 'jwt-decode'

Vue.use(VueSession)

Vue.use(Vuex)

export default new Vuex.Store({
    state : {
        token: null,
        isLogin : false,
    },
// mutations : state를 변화시키기 위한 메서드(함수)
 mutations : {
    // 첫번째 인자는 state
    // 이후 인자는 payload(즉, 매개변수)
    login(state, token) {
        state.token = token
        state.isLogin = true
    },
    logout(state){
        state.token = null
        state.isLogin = false
    }
},
 actions : {
    // 첫번째 인자는 context (다양한)
    // 이후 인자는 payload(매개변수)
    login(context, token) {
        // mutation 호출 -> commit
        context.commit('login',token)
    },
    logout(context) {
        context.commit('logout')
        sessionStorage.clear()
    }
},
 getters : {
        tokenval(state) {
            return jwtDecode(state.token)['access-Token']
        },
        options(state) {
            return {
                headers : {
                    Authorization : `JWT ${state.token}`,
                }
            }
        },
        memberid(state) {
            return jwtDecode(state.token)['access-Token'].memberid
        },
        name(state) {
            return jwtDecode(state.token)['access-Token'].name
        },
        username(state) {
            return jwtDecode(state.token)['access-token'].username
        },
        auth(state){
            return jwtDecode(state.token)['access-token'].auth
        },
        githubid(state){
            return jwtDecode(state.token)['access-token'].githubid
        }
    }
})