module algebra

class Group extends Monoid {
  inv(x: E): id * x == x
  left_inv(x: E): inv(x) * x == id
  right_inv(x: E): x * inv(x) == id

  @infix(/) div(x: E, y: E): E = x * inv(y)
}

class GroupHom {
  G: Group
  H: Group

  hom(G.E): H.E
  hom_respect_mul(x: G.E, y: G.E): hom(x G.* y) == hom(x) H.* hom(y)
}

id_group_hom(G: Group) = GroupHom {
  G, G
  hom(x) = x
  hom_respect_mul(x, y) = same(x G.* y)
}

group_category = Categroy {
  Object = Group
  Morphism = GroupHom
  id = id_group_hom

  then(f: GroupHom(G, H), g: GroupHom(H, K)) = GroupHom {
    G, K
    hom(x) = g.hom(f.hom(x))
    hom_respect_mul(x, y): g.hom(f.hom(x G.* y)) == g.hom(f.hom(x)) K.* g.hom(f.hom(y)) =
      same(g.hom(f.hom(x* y) H.* f.hom(y)))
  }

  left_id(f: GroupHom(G, H)): id_group_hom(G) | f == f =
    same(f)
  right_id(f: GroupHom(G, H)): f | id_group_hom(H) == f =
    same(f)

  associative(
    f: GroupHom(a, b),
    g: GroupHom(b, c),
    h: GroupHom(c, d),
  ): f | (g | h) == (f | g) | h = refl
}