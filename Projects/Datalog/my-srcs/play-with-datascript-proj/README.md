# play-with-datascript-proj




Core Structures:
   - Parser
      - ITraversable
      - IFindVars
      - IFindElements
   - Parser (Pull)
      - IPullSpecComponent
   - Query
      - IBinding
      - IContextResolve
      - IPostProcess
   - Query-V3
      - NativeColl
      - IRelation
      - IClause
   - DB
      - IDatom
      - ISearch
      - IIndexAccess
      - IDB


ITraversable
deftrecord
      - Placeholder [])
      - Variable    [symbol])
      - SrcVar      [symbol])
      - DefaultSrc  [])
      - RulesVar    [])
      - Constant    [value])
      - PlainSymbol [symbol])
      - RuleVars [required free])

      - BindIgnore [])
      - BindScalar [variable])
      - BindTuple  [bindings])
      - BindColl   [binding])

      - Aggregate [fn args] IFindVars (-find-vars [_] (-find-vars (last args))))
      - Pull [source variable pattern] IFindVars (-find-vars [_] (-find-vars variable)))

      - FindRel [elements] IFindElements (find-elements [_] elements))
      - FindColl [element] IFindElements (find-elements [_] [element]))
      - FindScalar [element] IFindElements (find-elements [_] [element]))
      - FindTuple [elements] IFindElements (find-elements [_] elements))

      - ReturnMap [type symbols])

      - Pattern   [source pattern])
      - Predicate [fn args])
      - Function  [fn args binding])
      - RuleExpr  [source name args]) ;; TODO rule with constant or '_' as argument
      - Not       [source vars clauses])
      - Or        [source rule-vars clauses])
      - And       [clauses])

      - RuleBranch [vars clauses])
      - Rule [name branches])

      - Query [qfind qwith qreturn-map qin qwhere])
