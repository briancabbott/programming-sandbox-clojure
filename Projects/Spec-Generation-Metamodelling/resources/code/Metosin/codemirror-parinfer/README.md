# Codemirror-parinfer


**Outdated, check https://github.com/shaunlebron/parinfer-codemirror**

This library is an extraction of code from Parinfer-site of the parinfer editor
for codemirror.

Metosin fork removes the global state and simplifies code quite a bit. Several
workarounds were removed, and it is possible that this breaks the code
in some cases. The simplified API should make it easy to use this with
for example Reagent. For example, see [komponentit](http://metosin.github.io/komponentit/#!/example.codemirror).
