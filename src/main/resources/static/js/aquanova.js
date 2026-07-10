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
