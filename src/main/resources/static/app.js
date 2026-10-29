const form = document.querySelector('#incidentForm');
const demoButton = document.querySelector('#demoButton');
const result = document.querySelector('#result');
const emptyState = document.querySelector('#emptyState');

const agentLabels = {
    TRIAGE: 'TR',
    RUNBOOK_RETRIEVAL: 'RG',
    SAFETY_REVIEW: 'SF',
    RESOLUTION_PLANNING: 'PL'
};

async function requestAnalysis(url, options = {}) {
    setBusy(true);
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.detail || 'Analysis failed');
        }
        render(await response.json());
    } catch (error) {
        emptyState.hidden = true;
        result.hidden = false;
        result.innerHTML = `<div class="empty-state"><h3>Unable to analyze</h3><p>${escapeHtml(error.message)}</p></div>`;
    } finally {
        setBusy(false);
    }
}

demoButton.addEventListener('click', () => requestAnalysis('/api/incidents/demo', { method: 'POST' }));

form.addEventListener('submit', (event) => {
    event.preventDefault();
    requestAnalysis('/api/incidents/analyze', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title: form.title.value,
            description: form.description.value,
            service: form.service.value
        })
    });
});

function setBusy(busy) {
    demoButton.disabled = busy;
    form.querySelector('button').disabled = busy;
    demoButton.firstChild.textContent = busy ? 'Agents are reasoning ' : 'Run live demo ';
}

function render(data) {
    emptyState.hidden = true;
    result.hidden = false;
    result.innerHTML = `
        <div class="result-head">
            <div><h3>${escapeHtml(data.title)}</h3><p>${escapeHtml(data.service)} · ${Math.round(data.confidence * 100)}% confidence</p></div>
            <span class="severity">${escapeHtml(data.severity)}</span>
        </div>
        <div class="runbook"><strong>Matched runbook:</strong> ${escapeHtml(data.matchedRunbook)}</div>
        ${data.agentSteps.map(step => `
            <div class="step">
                <b>${agentLabels[step.agent] || 'AI'}</b>
                <div><h4>${escapeHtml(step.summary)}</h4><p>${escapeHtml(step.evidence)}</p></div>
            </div>`).join('')}
        <h4 class="actions-title">Proposed actions</h4>
        <div id="actions">${actionsMarkup(data)}</div>`;
}

function actionsMarkup(data) {
    return data.actions.map(action => `
        <div class="action">
            <div>
                <h4>${escapeHtml(action.title)} · ${escapeHtml(action.risk)}</h4>
                <p>${escapeHtml(action.rationale)}</p>
                <code>${escapeHtml(action.command)}</code>
            </div>
            ${action.status === 'APPROVED'
                ? '<span class="approved">✓ APPROVED</span>'
                : `<button class="approve" onclick="approve('${data.incidentId}', '${action.id}')">Approve</button>`}
        </div>`).join('');
}

async function approve(incidentId, actionId) {
    await requestAnalysis(`/api/incidents/${incidentId}/actions/${actionId}/approve`, { method: 'POST' });
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}
