/* AquaNova — comportements partagés des pages liste.
   La pagination et le sélecteur "Lignes par page" reconstruisent l'URL courante
   en ne changeant que page/size : tous les filtres actifs sont préservés. */

document.addEventListener('click', function (e) {
    const lien = e.target.closest('.js-page-link');
    if (!lien) {
        return;
    }
    e.preventDefault();
    if (lien.parentElement.classList.contains('disabled')
            || lien.parentElement.classList.contains('active')) {
        return;
    }
    const url = new URL(window.location.href);
    url.searchParams.set('page', lien.dataset.page);
    window.location.href = url.toString();
});

document.addEventListener('change', function (e) {
    if (!e.target.classList.contains('js-page-size')) {
        return;
    }
    const url = new URL(window.location.href);
    url.searchParams.set('size', e.target.value);
    url.searchParams.set('page', '0');
    window.location.href = url.toString();
});

/* AquaNova — barre latérale rétractable.
   Sur grand écran, le bouton réduit la sidebar à un rail d'icônes et l'état est mémorisé
   d'une page à l'autre (localStorage). Sous 992px, le même bouton ouvre ou ferme la
   sidebar par-dessus le contenu : réduire à un rail n'aurait pas de sens sur un
   petit écran, et l'état n'y est volontairement pas mémorisé. */

(function () {
    const CLE = 'aquanova.sidebar.reduite';
    const SEUIL_MOBILE = 992;

    const shell = document.querySelector('.app-shell');
    const bouton = document.getElementById('sidebarToggle');
    const voile = document.getElementById('sidebarBackdrop');
    if (!shell || !bouton) {
        return;
    }

    const estMobile = () => window.innerWidth < SEUIL_MOBILE;

    if (localStorage.getItem(CLE) === '1') {
        shell.classList.add('sidebar-collapsed');
    }

    bouton.addEventListener('click', function () {
        if (estMobile()) {
            shell.classList.toggle('sidebar-open');
            return;
        }
        const reduite = shell.classList.toggle('sidebar-collapsed');
        localStorage.setItem(CLE, reduite ? '1' : '0');
    });

    if (voile) {
        voile.addEventListener('click', () => shell.classList.remove('sidebar-open'));
    }

    /* Sidebar réduite : les sous-menus sont masqués, cliquer sur une section ne
       montrerait donc rien. On rouvre d'abord la sidebar, puis on laisse Bootstrap
       dérouler la section. */
    document.querySelectorAll('.sidebar-section-toggle').forEach(function (section) {
        section.addEventListener('click', function () {
            if (!estMobile() && shell.classList.contains('sidebar-collapsed')) {
                shell.classList.remove('sidebar-collapsed');
                localStorage.setItem(CLE, '0');
            }
        });
    });

    /* Sur mobile, choisir une page referme la sidebar : sans cela, elle resterait
       ouverte par-dessus la page qu'on vient de demander. */
    document.querySelectorAll('.sidebar-link').forEach(function (lien) {
        lien.addEventListener('click', function () {
            if (estMobile()) {
                shell.classList.remove('sidebar-open');
            }
        });
    });

    /* En repassant en grand écran, on abandonne l'état "ouverte en surimpression". */
    window.addEventListener('resize', function () {
        if (!estMobile()) {
            shell.classList.remove('sidebar-open');
        }
    });
})();
