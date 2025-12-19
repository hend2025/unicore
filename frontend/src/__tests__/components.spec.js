/**
 * Layout 子组件单元测试
 * 使用 Vitest 进行测试
 */
import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { defineComponent, h } from 'vue'
import UserDropdown from '@/layout/components/UserDropdown.vue'

// Mock Element Plus 组件
vi.mock('element-plus', () => ({
    ElDropdown: defineComponent({
        setup(props, { slots }) {
            return () => h('div', { class: 'el-dropdown' }, slots.default?.())
        }
    }),
    ElDropdownMenu: defineComponent({
        setup(props, { slots }) {
            return () => h('div', { class: 'el-dropdown-menu' }, slots.default?.())
        }
    }),
    ElDropdownItem: defineComponent({
        props: ['command'],
        emits: ['click'],
        setup(props, { slots, emit }) {
            return () => h('div', {
                class: 'el-dropdown-item',
                onClick: () => emit('click', props.command)
            }, slots.default?.())
        }
    }),
    ElAvatar: defineComponent({
        props: ['size', 'icon'],
        setup() {
            return () => h('span', { class: 'el-avatar' })
        }
    })
}))

describe('UserDropdown', () => {
    it('应该正确渲染用户姓名', () => {
        const wrapper = mount(UserDropdown, {
            props: {
                userInfo: { realName: '张三', userName: 'zhangsan' }
            }
        })

        expect(wrapper.text()).toContain('张三')
    })

    it('应该优先显示 realName', () => {
        const wrapper = mount(UserDropdown, {
            props: {
                userInfo: { realName: '李四', userName: 'lisi' }
            }
        })

        expect(wrapper.find('.username').text()).toBe('李四')
    })

    it('没有 realName 时应该显示 userName', () => {
        const wrapper = mount(UserDropdown, {
            props: {
                userInfo: { userName: 'admin' }
            }
        })

        expect(wrapper.find('.username').text()).toBe('admin')
    })

    it('没有用户信息时应该显示"未登录"', () => {
        const wrapper = mount(UserDropdown, {
            props: {
                userInfo: {}
            }
        })

        expect(wrapper.find('.username').text()).toBe('未登录')
    })
})

describe('常量测试', () => {
    it('STATUS 常量应该定义正确', async () => {
        const { STATUS } = await import('@/constants')

        expect(STATUS.NORMAL).toBe('1')
        expect(STATUS.DISABLED).toBe('0')
    })

    it('ROUTES 常量应该定义正确', async () => {
        const { ROUTES } = await import('@/constants')

        expect(ROUTES.LOGIN).toBe('/login')
        expect(ROUTES.HOME).toBe('/home')
    })

    it('STORAGE_KEYS 常量应该定义正确', async () => {
        const { STORAGE_KEYS } = await import('@/constants')

        expect(STORAGE_KEYS.TOKEN).toBe('access_token')
        expect(STORAGE_KEYS.ACTIVE_SYSTEM).toBe('activeSystem')
    })
})

describe('routes 配置测试', () => {
    it('componentMap 应该包含所有内部页面', async () => {
        const { componentMap } = await import('@/router/routes')

        expect(componentMap['/home']).toBeDefined()
        expect(componentMap['system/user']).toBeDefined()
        expect(componentMap['system/role']).toBeDefined()
    })

    it('menuTitleMap 应该包含正确的标题', async () => {
        const { menuTitleMap } = await import('@/router/routes')

        expect(menuTitleMap['/home']).toBe('首页')
        expect(menuTitleMap['system/user']).toBe('用户管理')
        expect(menuTitleMap['log/operlog']).toBe('操作日志')
    })

    it('generateChildRoutes 应该生成正确的路由配置', async () => {
        const { generateChildRoutes } = await import('@/router/routes')
        const routes = generateChildRoutes()

        expect(routes.length).toBeGreaterThan(0)
        expect(routes.every(r => r.path && r.component && r.meta)).toBe(true)
    })
})
