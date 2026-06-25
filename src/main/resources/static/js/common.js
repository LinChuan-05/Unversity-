/**
 * 通用 Ajax 工具
 */
const BASE = '/myWeb';

// GET 请求
async function $get(url) {
    const resp = await fetch(BASE + url, { credentials: 'same-origin' });
    if (!resp.ok) {
        const text = await resp.text();
        throw new Error(text || resp.statusText);
    }
    return resp.json();
}

// POST 请求 (表单格式)
async function $post(url, data) {
    const formData = new URLSearchParams();
    for (let key in data) {
        formData.append(key, data[key]);
    }
    const resp = await fetch(BASE + url, {
        method: 'POST',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData
    });
    if (!resp.ok) {
        const text = await resp.text();
        throw new Error(text || resp.statusText);
    }
    return resp.json();
}

// POST 请求 (JSON 格式)
async function $postJson(url, data) {
    const resp = await fetch(BASE + url, {
        method: 'POST',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    return resp.json();
}

// DELETE 请求
async function $del(url, data) {
    const params = new URLSearchParams(data);
    const resp = await fetch(BASE + url + '?' + params, {
        method: 'DELETE',
        credentials: 'same-origin'
    });
    if (!resp.ok) {
        const text = await resp.text();
        throw new Error(text || resp.statusText);
    }
    return resp.json();
}

// PUT 请求 (表单格式)
async function $put(url, data) {
    const formData = new URLSearchParams();
    for (let key in data) {
        formData.append(key, data[key]);
    }
    const resp = await fetch(BASE + url, {
        method: 'PUT',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData
    });
    return resp.json();
}

// 获取当前用户角色
async function loadSidebar() {
    const res = await $get('/api/currentUser');
    const sidebar = document.getElementById('sidebar');
    if (!sidebar) return;
    let html = '<ul class="sidebar">';
    if (res.code === 200 && res.data && res.data.role === 'admin') {
        html += `<div class="menu-title">用户管理</div>
            <li class="menu-group"><a href="/myWeb/user_add.html" target="right" class="menu-item"><i class="bi bi-person-plus"></i> 用户注册</a></li>
            <li class="menu-group"><a href="/myWeb/user_list.html" target="right" class="menu-item"><i class="bi bi-people"></i> 用户列表</a></li>
            <li class="menu-group"><a href="/myWeb/class_list.html" target="right" class="menu-item"><i class="bi bi-diagram-3"></i> 班级管理</a></li>
            <div class="menu-title">考试管理</div>
            <li class="menu-group"><a href="/myWeb/exam_manage.html" target="right" class="menu-item"><i class="bi bi-journals"></i> 科目设置</a></li>
            <li class="menu-group"><a href="/myWeb/reset_exam.html" target="right" class="menu-item"><i class="bi bi-arrow-repeat"></i> 重考管理</a></li>
            <li class="menu-group"><a href="/myWeb/manual_grading.html" target="right" class="menu-item"><i class="bi bi-pencil"></i> 人工批阅</a></li>
            <div class="menu-title">试题管理</div>
            <li class="menu-group"><a href="/myWeb/question_add.html" target="right" class="menu-item"><i class="bi bi-plus-circle"></i> 添加试题</a></li>
            <li class="menu-group"><a href="/myWeb/question_list.html" target="right" class="menu-item"><i class="bi bi-list-ul"></i> 试题列表</a></li>`;
    }
    if (res.code === 200 && res.data && res.data.role === 'student') {
        html += `<div class="menu-title">考试</div>
            <li class="menu-group"><a href="/myWeb/exam_select.html" target="right" class="menu-item"><i class="bi bi-pencil-square"></i> 参加考试</a></li>
            <li class="menu-group"><a href="/myWeb/score_history.html" target="right" class="menu-item"><i class="bi bi-bar-chart"></i> 成绩查询</a></li>
            <li class="menu-group"><a href="/myWeb/wrong_answers.html" target="right" class="menu-item"><i class="bi bi-journal-x"></i> 错题集</a></li>`;
    }
    html += '</ul>';
    sidebar.innerHTML = html;
}
